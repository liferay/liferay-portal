/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.exception.NoSuchObjectFieldException;
import com.liferay.object.exception.NoSuchObjectFolderException;
import com.liferay.object.exception.ObjectDefinitionAccountEntryRestrictedException;
import com.liferay.object.exception.ObjectDefinitionActiveException;
import com.liferay.object.exception.ObjectDefinitionClassNameException;
import com.liferay.object.exception.ObjectDefinitionEnableFriendlyURLCustomizationException;
import com.liferay.object.exception.ObjectDefinitionEnableLocalizationException;
import com.liferay.object.exception.ObjectDefinitionEnableObjectEntryHistoryException;
import com.liferay.object.exception.ObjectDefinitionExternalReferenceCodeException;
import com.liferay.object.exception.ObjectDefinitionLabelException;
import com.liferay.object.exception.ObjectDefinitionModifiableException;
import com.liferay.object.exception.ObjectDefinitionNameException;
import com.liferay.object.exception.ObjectDefinitionPanelCategoryKeyException;
import com.liferay.object.exception.ObjectDefinitionPluralLabelException;
import com.liferay.object.exception.ObjectDefinitionRootObjectDefinitionIdException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.exception.ObjectDefinitionSettingNameException;
import com.liferay.object.exception.ObjectDefinitionSettingValueException;
import com.liferay.object.exception.ObjectDefinitionStatusException;
import com.liferay.object.exception.ObjectDefinitionSystemException;
import com.liferay.object.exception.ObjectDefinitionVersionException;
import com.liferay.object.exception.ObjectFieldRelationshipTypeException;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.ObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectActionLocalServiceUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.BaseSystemObjectDefinitionManager;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.Column;
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
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.UserNotificationEventTable;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.hamcrest.CoreMatchers;

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
@FeatureFlags({"LPD-21926", "LPD-34594"})
@RunWith(Arquillian.class)
public class ObjectDefinitionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

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

	@Test
	public void testAddCustomObjectDefinition() throws Exception {

		// Enable friendly URL customization

		AssertUtils.assertFailure(
			ObjectDefinitionEnableFriendlyURLCustomizationException.class,
			"Enable friendly URL customization is only allowed for object " +
				"definitions with the default storage type",
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList()));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, true, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList());

		Assert.assertTrue(objectDefinition.isEnableFriendlyURLCustomization());

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
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, "", ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(),
						StringUtil.randomId()))));

		// No object scope provider found with key

		String scope = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"No object scope provider found with key " + scope,
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(),
						StringUtil.randomId()))));

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			StringBundler.concat(
				"Scope \"", ObjectDefinitionConstants.SCOPE_SITE,
				"\" cannot be associated with storage type \"",
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE),
			() -> _objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
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
						RandomTestUtil.randomString(),
						StringUtil.randomId()))));

		// Name, database table, resources, and status

		objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
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
						false)));

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Charlie")
			).name(
				"charlie"
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).required(
				true
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
			_hasColumn(objectDefinition.getDBTableName(), "baker"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getDBTableName(), "baker_"));
		Assert.assertFalse(
			_hasColumn(objectDefinition.getDBTableName(), "charlie"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getDBTableName(), "charlie_"));
		Assert.assertFalse(
			_hasColumn(objectDefinition.getExtensionDBTableName(), "dog"));
		Assert.assertTrue(
			_hasColumn(objectDefinition.getExtensionDBTableName(), "dog_"));
		Assert.assertTrue(_hasTable(objectDefinition.getDBTableName()));
		Assert.assertTrue(
			_hasTable(objectDefinition.getExtensionDBTableName()));

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
				if (nodeObjectDefinition.isRootNode()) {
					Assert.assertEquals(
						4,
						_resourceActionLocalService.getResourceActionsCount(
							nodeObjectDefinition.getClassName()));
					Assert.assertEquals(
						2,
						_resourceActionLocalService.getResourceActionsCount(
							nodeObjectDefinition.getResourceName()));
				}
				else {
					Assert.assertEquals(
						0,
						_resourceActionLocalService.getResourceActionsCount(
							nodeObjectDefinition.getClassName()));
					Assert.assertEquals(
						0,
						_resourceActionLocalService.getResourceActionsCount(
							nodeObjectDefinition.getResourceName()));
				}

				Assert.assertEquals(
					6,
					_resourceActionLocalService.getResourceActionsCount(
						nodeObjectDefinition.getPortletId()));
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

	@Test
	public void testAddObjectDefinitionWithObjectDefinitionSettings()
		throws Exception {

		String randomName = ObjectDefinitionTestUtil.getRandomName();

		AssertUtils.assertFailure(
			ObjectDefinitionSettingNameException.NotAllowedNames.class,
			StringBundler.concat(
				"The settings ",
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				" are not allowed for object definition ", randomName),
			() -> _addCustomObjectDefinition(
				randomName, ObjectDefinitionConstants.SCOPE_COMPANY,
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
				" are not allowed for object definition ", randomName),
			() -> _addCustomObjectDefinition(
				randomName, ObjectDefinitionConstants.SCOPE_SITE,
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
				"The settings ", randomName,
				" are not allowed for object definition ", randomName),
			() -> _addCustomObjectDefinition(
				randomName, ObjectDefinitionConstants.SCOPE_DEPOT,
				Collections.singletonList(
					new ObjectDefinitionSettingBuilder(
					).name(
						randomName
					).value(
						randomName
					).build())));
		AssertUtils.assertFailure(
			ObjectDefinitionSettingNameException.NotAllowedNames.class,
			StringBundler.concat(
				"The settings ",
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				" are not allowed for object definition ", randomName),
			() -> _addCustomObjectDefinition(
				randomName, ObjectDefinitionConstants.SCOPE_DEPOT,
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

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			Collections.emptyMap(), ServiceContextTestUtil.getServiceContext());

		long depotGroupId = depotEntry.getGroupId();

		AssertUtils.assertFailure(
			ObjectDefinitionSettingValueException.InvalidValue.class,
			StringBundler.concat(
				"The value ", TestPropsValues.getGroupId(), " of setting \"",
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				"\" is invalid for object definition \"", randomName, "\""),
			() -> _addCustomObjectDefinition(
				randomName, ObjectDefinitionConstants.SCOPE_DEPOT,
				List.of(
					new ObjectDefinitionSettingBuilder(
					).name(
						ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
					).value(
						StringBundler.concat(
							depotGroupId, StringPool.COMMA,
							TestPropsValues.getGroupId())
					).build())));

		ObjectDefinition objectDefinition = _addCustomObjectDefinition(
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
			objectDefinition.getObjectDefinitionSettings(),
			Collections.singletonMap(
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				StringPool.TRUE));

		objectDefinition = _addCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			ObjectDefinitionConstants.SCOPE_DEPOT,
			List.of(
				new ObjectDefinitionSettingBuilder(
				).name(
					ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS
				).value(
					String.valueOf(depotGroupId)
				).build()));

		_assertObjectDefinitionSettingsValues(
			objectDefinition.getObjectDefinitionSettings(),
			Collections.singletonMap(
				ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
				String.valueOf(depotGroupId)));
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

	@FeatureFlags("LPD-32050")
	@Test
	public void testAddSystemObjectDefinition() throws Exception {

		// Enable friendly URL customization

		AssertUtils.assertFailure(
			ObjectDefinitionEnableFriendlyURLCustomizationException.class,
			"Enable friendly URL customization is not allowed for " +
				"unmodifiable system object definitions",
			() -> _objectDefinitionLocalService.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0,
				ObjectDefinitionTestUtil.getRandomName(), null, false, true,
				true, false, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionTestUtil.getRandomName(), null, null, null,
				null, RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.emptyList()));

		// Enable localization

		AssertUtils.assertFailure(
			ObjectDefinitionEnableLocalizationException.class,
			"Enable localization is not allowed for unmodifiable object " +
				"definitions",
			() -> ObjectDefinitionLocalServiceUtil.addSystemObjectDefinition(
				null, TestPropsValues.getUserId(), 0,
				ObjectDefinitionTestUtil.getRandomName(), null, false, false,
				true, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, "Test", null, null, null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				WorkflowConstants.STATUS_APPROVED, Collections.emptyList(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						StringUtil.randomId()
					).build())));

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

		ObjectDefinition objectDefinition =
			_addUnmodifiableSystemObjectDefinition("Test");

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
	public void testBindObjectDefinitions() throws Exception {

		// Bind object definitions creating a new hierarchical structure

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A");
		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AA");

		ObjectRelationship objectRelationshipA_AA =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinitionA,
				objectDefinitionAA,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT);

		_testBindObjectDefinitions(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build(),
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionAA,
					ObjectDefinitionTestUtil.addCustomObjectDefinition("AAA"),
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT),
				objectRelationshipA_AA),
			objectDefinitionA.getObjectDefinitionId());

		// Bind one object definition to an existing hierarchical structure

		_testBindObjectDefinitions(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA", "AAB"}
			).put(
				"AAA", new String[0]
			).put(
				"AAB", new String[0]
			).build(),
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionAA,
					ObjectDefinitionTestUtil.addCustomObjectDefinition("AAB"),
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT)),
			objectDefinitionA.getObjectDefinitionId());

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAB"},
			_objectEntryLocalService, _objectRelationshipLocalService);
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
					user.getUserId(), 0, null, false, false, true, false, false,
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
							StringUtil.randomId())));

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

		ObjectDefinition objectDefinition = _addCustomObjectDefinition("Test");

		objectDefinition =
			_objectDefinitionLocalService.updateRootObjectDefinitionId(
				objectDefinition.getObjectDefinitionId(),
				objectDefinition.getObjectDefinitionId());

		ObjectDefinition finalObjectDefinition = objectDefinition;

		AssertUtils.assertFailure(
			ObjectDefinitionRootObjectDefinitionIdException.class,
			"To delete this object, you must first disable inheritance and " +
				"delete its relationships",
			() -> _objectDefinitionLocalService.deleteObjectDefinition(
				finalObjectDefinition));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());

		_objectDefinitionLocalService.updateRootObjectDefinitionId(
			objectDefinition.getObjectDefinitionId(), 0);

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

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
	}

	@Test
	public void testEnableAccountEntryRestrictedForNondefaultStorageType()
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false, LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
				null, LocalizedMapUtil.getLocalizedMap("Ables"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList());

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
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false, LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
				null, LocalizedMapUtil.getLocalizedMap("Ables"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList());

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
	public void testPublishCustomObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).localized(
						true
					).build()));

		AssertUtils.assertFailure(
			ObjectDefinitionEnableLocalizationException.class,
			"You cannot disable entry translation for the object definition " +
				"because translation is enabled for custom fields",
			() -> _objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId()));

		ObjectDefinition objectDefinition2 = null;
		ObjectDefinition objectDefinition3 = null;

		try {
			objectDefinition2 = _publishCustomObjectDefinition(false);

			Assert.assertNull(
				IndexerRegistryUtil.getIndexer(
					objectDefinition2.getClassName()));

			objectDefinition3 = _publishCustomObjectDefinition(true);

			Assert.assertNotNull(
				IndexerRegistryUtil.getIndexer(
					objectDefinition3.getClassName()));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition2);
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition3);
		}
	}

	@Test
	public void testPublishNode() throws Exception {
		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A");

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionA.getObjectDefinitionId());

		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AA");

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA)));

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAA.getObjectDefinitionId());

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testPublishNodeWithDraftDescendantNodes() throws Exception {
		_testCreateObjectDefinitionTree(
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build());
		_testCreateObjectDefinitionTree(
			false,
			LinkedHashMapBuilder.put(
				"AAA", new String[] {"AAAA", "AAAB"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAB", new String[] {"AAABA"}
			).put(
				"AAAAA", new String[0]
			).put(
				"AAABA", new String[0]
			).build());

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AA"),
					objectDefinitionAAA)));

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAA", new String[] {"AAAA", "AAAB"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAB", new String[] {"AAABA"}
			).put(
				"AAAAA", new String[0]
			).put(
				"AAABA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAAA.getObjectDefinitionId());

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAAA");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAAAB =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAAB");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAAB", new String[] {"AAABA"}
			).put(
				"AAABA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAAB.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAB", "C_AAAAA", "C_AAABA"
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testPublishNodeWithPublishedDescendantNodes() throws Exception {
		_testCreateObjectDefinitionTree(
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build());

		_addObjectAction("C_AA");

		_assertModelResourceNames(ListUtil.fromArray("C_A", "C_AA"));
		_assertRootDescendantNodeObjectDefinition("C_AA");

		_testCreateObjectDefinitionTree(
			true,
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build());

		_addObjectAction("C_AAAAA");

		_assertModelResourceNames(ListUtil.fromArray("C_AAAA", "C_AAAAA"));
		_assertRootDescendantNodeObjectDefinition("C_AAAAA");

		ObjectDefinition objectDefinitionAAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAA");

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AA"),
					objectDefinitionAAA),
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionAAA,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AAAA"))));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAAA.getObjectDefinitionId());

		_assertModelResourceNames(ListUtil.fromArray("C_A", "C_AA", "C_AAAAA"));
		_assertRootDescendantNodeObjectDefinition("C_AAA");
		_assertRootDescendantNodeObjectDefinition("C_AAAA");

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[] {"AAAA"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.unbind(
			objectDefinitionA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_assertModelResourceNames(ListUtil.fromArray("C_A"));
		_assertModelResourceNames(ListUtil.fromArray("C_AA", "C_AAAAA"));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testPublishRootNode() throws Exception {

		// publish a draft object definition

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A");

		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AA");

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAA.getObjectDefinitionId());

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA)));

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionA.getObjectDefinitionId());

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// publish a draft object definition from a draft object definition tree

		_testCreateObjectDefinitionTree(
			false,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build());
		_testCreateObjectDefinitionTree(
			true,
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build());

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AAA"),
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AAAA"))));

		objectDefinitionA = _objectDefinitionLocalService.getObjectDefinition(
			TestPropsValues.getCompanyId(), "C_A");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAAA");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAAA.getObjectDefinitionId());

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAA", new String[] {"AAAA"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testSystemObjectFields() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList());

		_testSystemObjectFields(objectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		objectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), "Test", null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				Collections.<ObjectField>emptyList());

		_testSystemObjectFields(objectDefinition);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Test
	public void testUpdateCustomObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false, LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
				null, LocalizedMapUtil.getLocalizedMap("Ables"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SALESFORCE,
				Collections.emptyList(), Collections.emptyList());

		_assertLabelAndPluralLabel(objectDefinition, "Able", "Ables");

		Assert.assertFalse(objectDefinition.isActive());
		Assert.assertFalse(objectDefinition.isEnableFriendlyURLCustomization());
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
			() -> _updateObjectDefinition(
				null, objectDefinitionId, 0, 0, true, true,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able",
				LocalizedMapUtil.getLocalizedMap("Ables"), scope, status));
		AssertUtils.assertFailure(
			ObjectDefinitionEnableObjectEntryHistoryException.class,
			"Enable object entry history is only allowed for object " +
				"definitions with the default storage type",
			() -> _updateObjectDefinition(
				null, objectDefinitionId, 0, 0, false, true,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able",
				LocalizedMapUtil.getLocalizedMap("Ables"), scope, status));
		AssertUtils.assertFailure(
			ObjectDefinitionExternalReferenceCodeException.
				MustNotStartWithPrefix.class,
			"The prefix L_ is reserved",
			() -> _updateObjectDefinition(
				"L_INVALID_ERC_TEST", objectDefinitionId, 0, 0, false, false,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able",
				LocalizedMapUtil.getLocalizedMap("Ables"), scope, status));

		objectDefinition.setStorageType(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		Assert.assertEquals(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			objectDefinition.getStorageType());

		AssertUtils.assertFailure(
			NoSuchObjectFieldException.class, null,
			() -> _updateObjectDefinition(
				null, objectDefinitionId, RandomTestUtil.randomLong(),
				RandomTestUtil.randomLong(), false, false,
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
					objectDefinition.getClassName(), true, false, false, true,
					false, false, false,
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
					Collections.emptyList());

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
				true, false, true, false, false, false, false,
				LocalizedMapUtil.getLocalizedMap("Able"), "Able", null, null,
				false, LocalizedMapUtil.getLocalizedMap("Ables"),
				objectDefinition.getScope(), objectDefinition.getStatus(),
				Collections.emptyList());

		Assert.assertEquals(
			externalReferenceCode, objectDefinition.getExternalReferenceCode());
		Assert.assertEquals(0, objectDefinition.getDescriptionObjectFieldId());
		Assert.assertEquals(
			objectFolder.getObjectFolderId(),
			objectDefinition.getObjectFolderId());
		Assert.assertEquals(0, objectDefinition.getTitleObjectFieldId());
		Assert.assertFalse(objectDefinition.isActive());
		Assert.assertTrue(objectDefinition.isEnableFriendlyURLCustomization());
		Assert.assertFalse(objectDefinition.isEnableIndexSearch());
		Assert.assertFalse(objectDefinition.isEnableObjectEntryHistory());
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
				false, objectDefinition.isActive(), null, true, false, false,
				true, false, false, true,
				LocalizedMapUtil.getLocalizedMap("Baker"), "Baker", null, null,
				false, LocalizedMapUtil.getLocalizedMap("Bakers"),
				objectDefinition.getScope(), objectDefinition.getStatus(),
				Collections.emptyList());

		_assertLabelAndPluralLabel(objectDefinition, "Baker", "Bakers");

		Assert.assertFalse(objectDefinition.isActive());
		Assert.assertFalse(objectDefinition.isEnableFriendlyURLCustomization());
		Assert.assertTrue(objectDefinition.isEnableIndexSearch());
		Assert.assertTrue(objectDefinition.isEnableObjectEntryHistory());
		Assert.assertEquals("C_Baker", objectDefinition.getName());

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		objectDefinition =
			_objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition.getObjectDefinitionId(), 0, 0, 0, 0,
				false, true, objectDefinition.getClassName(), true, false, true,
				false, false, false, true,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie", null,
				null, false, LocalizedMapUtil.getLocalizedMap("Charlies"),
				objectDefinition.getScope(), objectDefinition.getStatus(),
				Collections.emptyList());

		_assertLabelAndPluralLabel(objectDefinition, "Charlie", "Charlies");

		Assert.assertTrue(objectDefinition.isActive());
		Assert.assertTrue(objectDefinition.isEnableFriendlyURLCustomization());
		Assert.assertTrue(objectDefinition.isEnableIndexSearch());
		Assert.assertTrue(objectDefinition.isEnableObjectEntryHistory());
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
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false, LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
				null, LocalizedMapUtil.getLocalizedMap("Ables"), false,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), Collections.emptyList());

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
		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				StringUtil.randomId(), TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		AssertUtils.assertFailure(
			ObjectDefinitionPanelCategoryKeyException.class,
			"Panel category key cannot be changed when the object definition " +
				"is a root descendant node",
			() -> _testUpdateRootDescendantObjectDefinition(
				objectDefinition2, RandomTestUtil.randomString()));

		_testUpdateRootDescendantObjectDefinition(objectDefinition2, null);
		_testUpdateRootDescendantObjectDefinition(
			objectDefinition2, StringPool.BLANK);

		objectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getObjectRelationshipId(),
				objectRelationship.getParameterObjectFieldId(),
				objectRelationship.getDeletionType(), false,
				objectRelationship.getLabelMap(), null);

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
	}

	@Test
	public void testUpdateRootObjectDefinitionId() throws Exception {
		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		AssertUtils.assertFailure(
			ObjectDefinitionRootObjectDefinitionIdException.class,
			"Object definition " + objectDefinition2.getObjectDefinitionId() +
				" is not a root object definition",
			() -> _objectDefinitionLocalService.updateRootObjectDefinitionId(
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId()));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
	}

	@FeatureFlags("LPD-32050")
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
				false, true, false, false, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				RandomTestUtil.randomString(), null, null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList()));

		// Object folder does not exist

		long objectFolderId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			NoSuchObjectFolderException.class,
			"No ObjectFolder exists with the primary key " + objectFolderId,
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0,
				objectFolderId, 0, false, false,
				objectDefinition1.getClassName(), false, true, false, true,
				true, false, false, LocalizedMapUtil.getLocalizedMap("Charlie"),
				"Charlie", null, null, false,
				LocalizedMapUtil.getLocalizedMap("Charlie"),
				ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList()));

		// Modifiable system object definition must be published to be actived

		AssertUtils.assertFailure(
			ObjectDefinitionActiveException.class,
			"Object definitions must be published before being activated",
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0, 0, 0,
				false, true, objectDefinition1.getClassName(), false, true,
				false, true, true, false, false,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie", null,
				null, false, LocalizedMapUtil.getLocalizedMap("Charlies"),
				ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList()));

		// Label is null

		AssertUtils.assertFailure(
			ObjectDefinitionLabelException.class,
			"Label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0, 0, 0,
				false, false, objectDefinition1.getClassName(), false, true,
				false, true, true, false, false, null, "Charlie", null, null,
				false, LocalizedMapUtil.getLocalizedMap("Charlie"),
				ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList()));

		// Plural label is null

		AssertUtils.assertFailure(
			ObjectDefinitionPluralLabelException.class,
			"Plural label is null for locale " + LocaleUtil.US.getDisplayName(),
			() -> _objectDefinitionLocalService.updateCustomObjectDefinition(
				null, objectDefinition1.getObjectDefinitionId(), 0, 0, 0, 0,
				false, false, objectDefinition1.getClassName(), false, true,
				false, true, true, false, false,
				LocalizedMapUtil.getLocalizedMap("Charlie"), "Charlie", null,
				null, false, null, ObjectDefinitionConstants.SCOPE_SITE,
				objectDefinition1.getStatus(), Collections.emptyList()));

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
				objectDefinition2.getClassName(), false, true, false, true,
				true, false, false, LocalizedMapUtil.getLocalizedMap("Charlie"),
				"Charlie", null, null, false,
				LocalizedMapUtil.getLocalizedMap("Charlies"),
				objectDefinition2.getScope(), objectDefinition2.getStatus(),
				Collections.emptyList());

		_assertLabelAndPluralLabel(objectDefinition2, "Charlie", "Charlies");

		Assert.assertEquals(
			objectFolder.getObjectFolderId(),
			objectDefinition2.getObjectFolderId());
		Assert.assertFalse(objectDefinition2.isEnableCategorization());
		Assert.assertTrue(objectDefinition2.isEnableComments());
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
				objectField.getObjectFieldId(), Collections.emptyList());

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
			String name, String scope,
			List<ObjectDefinitionSetting> objectDefinitionSettings)
		throws Exception {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			TestPropsValues.getUserId(), 0, null, false, false, true, false,
			false, LocalizedMapUtil.getLocalizedMap(name), name, null, null,
			LocalizedMapUtil.getLocalizedMap(name), true, scope,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			objectDefinitionSettings,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())));
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
			TestPropsValues.getUserId(), 0, className, false, false, true,
			false, false, LocalizedMapUtil.getLocalizedMap(label), name, null,
			null, LocalizedMapUtil.getLocalizedMap(pluralLabel), true,
			ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())));
	}

	private void _addObjectAction(String objectDefinitionName)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		_objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, null,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"secret", "standalone"
			).put(
				"url", "https://standalone.com"
			).build(),
			false);
	}

	private ObjectFolder _addObjectFolder() throws Exception {
		return _objectFolderLocalService.addObjectFolder(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString());
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

	private void _assertModelResourceNames(List<String> objectDefinitionNames)
		throws Exception {

		Map<String, Set<String>> resourceReferences =
			ReflectionTestUtil.getFieldValue(
				_resourceActions, "_resourceReferences");

		ObjectDefinition rootObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionNames.get(0));

		List<String> modelResourceNames = ListUtil.filter(
			new ArrayList<>(
				resourceReferences.get(rootObjectDefinition.getPortletId())),
			resourceName -> StringUtil.startsWith(
				resourceName,
				ObjectDefinitionConstants.
					CLASS_NAME_PREFIX_CUSTOM_OBJECT_DEFINITION));

		Assert.assertEquals(
			modelResourceNames.toString(), objectDefinitionNames.size(),
			modelResourceNames.size());

		for (String objectDefinitionName : objectDefinitionNames) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					TestPropsValues.getCompanyId(), objectDefinitionName);

			Assert.assertThat(
				modelResourceNames,
				CoreMatchers.hasItem(objectDefinition.getClassName()));
		}
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

	private void _assertRootDescendantNodeObjectDefinition(String name)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), name);

		Assert.assertEquals(
			StringPool.BLANK, objectDefinition.getPanelCategoryKey());
		Assert.assertFalse(objectDefinition.isPortlet());

		Assert.assertNull(
			_resourceActionLocalService.fetchResourceAction(
				objectDefinition.getClassName(), ActionKeys.DELETE));
		Assert.assertNull(
			_resourceActionLocalService.fetchResourceAction(
				objectDefinition.getClassName(), ActionKeys.PERMISSIONS));
		Assert.assertNull(
			_resourceActionLocalService.fetchResourceAction(
				objectDefinition.getClassName(), ActionKeys.UPDATE));
		Assert.assertNull(
			_resourceActionLocalService.fetchResourceAction(
				objectDefinition.getClassName(), ActionKeys.VIEW));
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
				TestPropsValues.getUserId(), 0, null, false, false,
				enableIndexSearch, false, false,
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
						RandomTestUtil.randomString(), StringUtil.randomId())));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
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

	private void _testBindObjectDefinitions(
			Map<String, String[]> expectedMap,
			List<ObjectRelationship> objectRelationships,
			long rootObjectDefinitionId)
		throws Exception {

		TreeTestUtil.bind(_objectRelationshipLocalService, objectRelationships);

		for (ObjectRelationship objectRelationship : objectRelationships) {
			ObjectField objectField2 = _objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

			Assert.assertTrue(objectField2.isRequired());

			objectRelationship =
				_objectRelationshipLocalService.getObjectRelationship(
					objectRelationship.getObjectRelationshipId());

			Assert.assertEquals(
				objectRelationship.getDeletionType(),
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE);

			ObjectDefinition objectDefinition1 =
				_objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId1());

			if (objectDefinition1.isRootDescendantNode()) {
				Assert.assertFalse(objectDefinition1.isPortlet());
			}

			ObjectDefinition objectDefinition2 =
				_objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId2());

			if (objectDefinition2.isRootDescendantNode()) {
				Assert.assertFalse(objectDefinition2.isPortlet());
			}
		}

		TreeTestUtil.assertObjectDefinitionTree(
			expectedMap,
			_objectDefinitionTreeFactory.create(rootObjectDefinitionId),
			_objectDefinitionLocalService);
	}

	private void _testCreateObjectDefinitionTree(
			boolean published, Map<String, String[]> treeMap)
		throws Exception {

		TreeTestUtil.assertObjectDefinitionTree(
			treeMap,
			TreeTestUtil.createObjectDefinitionTree(
				_objectDefinitionLocalService, _objectRelationshipLocalService,
				published, treeMap),
			_objectDefinitionLocalService);
	}

	private void _testSystemObjectFields(ObjectDefinition objectDefinition)
		throws Exception {

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		Assert.assertNotNull(objectFields);

		boolean system = objectDefinition.isSystem();

		Assert.assertEquals(objectFields.toString(), 6, objectFields.size());

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
					objectDefinition2.getClassName(), true, false, false, true,
					false, false, false,
					LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
					null, false, LocalizedMapUtil.getLocalizedMap("Ables"),
					objectDefinition2.getScope(), objectDefinition2.getStatus(),
					Collections.emptyList());

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

	private void _testUpdateRootDescendantObjectDefinition(
			ObjectDefinition objectDefinition, String panelCategoryKey)
		throws Exception {

		objectDefinition = _objectDefinitionLocalService.fetchObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		_objectDefinitionLocalService.updateCustomObjectDefinition(
			objectDefinition.getExternalReferenceCode(),
			objectDefinition.getObjectDefinitionId(),
			objectDefinition.getAccountEntryRestrictedObjectFieldId(),
			objectDefinition.getDescriptionObjectFieldId(),
			objectDefinition.getObjectFolderId(),
			objectDefinition.getTitleObjectFieldId(),
			objectDefinition.isAccountEntryRestricted(),
			objectDefinition.isActive(), objectDefinition.getClassName(),
			objectDefinition.isEnableCategorization(),
			objectDefinition.isEnableComments(),
			objectDefinition.isEnableFriendlyURLCustomization(),
			objectDefinition.isEnableIndexSearch(),
			objectDefinition.isEnableLocalization(),
			objectDefinition.isEnableObjectEntryDraft(),
			objectDefinition.isEnableObjectEntryHistory(),
			objectDefinition.getLabelMap(), objectDefinition.getName(),
			objectDefinition.getPanelAppOrder(), panelCategoryKey,
			objectDefinition.isPortlet(), objectDefinition.getPluralLabelMap(),
			objectDefinition.getScope(), objectDefinition.getStatus(),
			Collections.emptyList());
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
			objectDefinition.isEnableFriendlyURLCustomization(),
			objectDefinition.isEnableIndexSearch(),
			objectDefinition.isEnableLocalization(),
			objectDefinition.isEnableObjectEntryDraft(),
			objectDefinition.isEnableObjectEntryHistory(),
			objectDefinition.getLabelMap(), objectDefinition.getShortName(),
			objectDefinition.getPanelAppOrder(),
			objectDefinition.getPanelCategoryKey(),
			objectDefinition.isPortlet(), objectDefinition.getPluralLabelMap(),
			objectDefinition.getScope(), objectDefinition.getStatus(),
			Collections.emptyList());
	}

	private ObjectDefinition _updateObjectDefinition(
			String externalReferenceCode, long objectDefinitionId,
			long descriptionObjectFieldId, long titleObjectFieldId,
			boolean enableFriendlyURLCustomization,
			boolean enableObjectEntryHistory, Map<Locale, String> labelMap,
			String name, Map<Locale, String> pluralLabelMap, String scope,
			int status)
		throws PortalException {

		return _objectDefinitionLocalService.updateCustomObjectDefinition(
			externalReferenceCode, objectDefinitionId, 0,
			descriptionObjectFieldId, 0, titleObjectFieldId, false, false, null,
			false, false, enableFriendlyURLCustomization, true, false, false,
			enableObjectEntryHistory, labelMap, name, null, null, false,
			pluralLabelMap, scope, status, Collections.emptyList());
	}

	private static ObjectFolder _defaultObjectFolder;

	@Inject
	private static ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

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

}