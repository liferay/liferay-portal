/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Gabriel Albuquerque
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ObjectDefinitionServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_adminUser = TestPropsValues.getUser();

		_objectFolder = _objectFolderLocalService.addObjectFolder(
			RandomTestUtil.randomString(), _adminUser.getUserId(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString());

		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleTestUtil.addResourcePermission(
			role, ObjectConstants.RESOURCE_NAME,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		RoleTestUtil.addResourcePermission(
			role, ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), ActionKeys.UPDATE);

		_userLocalService.addRoleUser(role.getRoleId(), _user2.getUserId());
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testAddCustomObjectDefinition() throws Exception {
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(), " must have ",
				"ADD_OBJECT_DEFINITION permission for com.liferay.object "),
			() -> _testAddCustomObjectDefinition(0, _user1));
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user2.getUserId(),
				" must have ADD_OBJECT_DEFINITION permission for ",
				"com.liferay.object.model.ObjectFolder ",
				_objectFolder.getObjectFolderId()),
			() -> _testAddCustomObjectDefinition(
				_objectFolder.getObjectFolderId(), _user2));

		_testAddCustomObjectDefinition(0, _adminUser);
		_testAddCustomObjectDefinition(
			_objectFolder.getObjectFolderId(), _adminUser);
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
	)
	@Test
	@TestInfo("LPD-66895")
	public void testAddCustomObjectDefinitionByCMSAdministratorRole()
		throws Exception {

		_setUpCMSContext();

		User user = UserTestUtil.addUser();

		Role role = RoleLocalServiceUtil.getRole(
			user.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		UserLocalServiceUtil.addRoleUser(role.getRoleId(), user);

		_testAddCustomObjectDefinition(0, user);

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				user.getCompanyId());

		_testAddCustomObjectDefinition(objectFolder.getObjectFolderId(), user);

		objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				user.getCompanyId());

		_testAddCustomObjectDefinition(objectFolder.getObjectFolderId(), user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(),
				" must have ADD_OBJECT_DEFINITION permission for ",
				"com.liferay.object.model.ObjectFolder ",
				_objectFolder.getObjectFolderId()),
			() -> _testAddCustomObjectDefinition(
				_objectFolder.getObjectFolderId(), user));
	}

	@Test
	public void testAddObjectDefinition() throws Exception {
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(), " must have ",
				"ADD_OBJECT_DEFINITION permission for com.liferay.object "),
			() -> _testAddObjectDefinition(0, _user1));
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user2.getUserId(),
				" must have ADD_OBJECT_DEFINITION permission for ",
				"com.liferay.object.model.ObjectFolder ",
				_objectFolder.getObjectFolderId()),
			() -> _testAddObjectDefinition(
				_objectFolder.getObjectFolderId(), _user2));

		_testAddObjectDefinition(0, _adminUser);
		_testAddObjectDefinition(_objectFolder.getObjectFolderId(), _adminUser);
	}

	@Test
	public void testAddSystemObjectDefinition() throws Exception {
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(), " must have ",
				"ADD_OBJECT_DEFINITION permission for com.liferay.object "),
			() -> _testAddSystemObjectDefinition(0, _user1));
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user2.getUserId(),
				" must have ADD_OBJECT_DEFINITION permission for ",
				"com.liferay.object.model.ObjectFolder ",
				_objectFolder.getObjectFolderId()),
			() -> _testAddSystemObjectDefinition(
				_objectFolder.getObjectFolderId(), _user2));

		_testAddSystemObjectDefinition(0, _adminUser);
		_testAddSystemObjectDefinition(
			_objectFolder.getObjectFolderId(), _adminUser);
	}

	@Test
	public void testDeleteObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(),
				" must have DELETE permission for ",
				"com.liferay.object.model.ObjectDefinition ",
				objectDefinition.getObjectDefinitionId()),
			() -> _testDeleteObjectDefinition(objectDefinition, _user1));

		_testDeleteObjectDefinition(
			_addCustomObjectDefinition(_adminUser), _adminUser);
		_testDeleteObjectDefinition(_addCustomObjectDefinition(_user1), _user1);
	}

	@Test
	public void testGetObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(), " must have VIEW permission for ",
				"com.liferay.object.model.ObjectDefinition ",
				objectDefinition.getObjectDefinitionId()),
			() -> _testGetObjectDefinition(objectDefinition, _user1));

		_testGetObjectDefinition(
			_addCustomObjectDefinition(_adminUser), _adminUser);
		_testGetObjectDefinition(_addCustomObjectDefinition(_user1), _user1);
	}

	@Test
	public void testPublishCustomObjectDefinition() throws Exception {
		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(),
				" must have PUBLISH_OBJECT_DEFINITION permission for ",
				"com.liferay.object "),
			() -> _testPublishCustomObjectDefinition(
				_addCustomObjectDefinition(_adminUser), _user1));

		_testPublishCustomObjectDefinition(
			_addCustomObjectDefinition(_adminUser), _adminUser);
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
	)
	@Test
	@TestInfo("LPD-66895")
	public void testPublishCustomObjectDefinitionByCMSAdministratorRole()
		throws Exception {

		_setUpCMSContext();

		User user = UserTestUtil.addUser();

		Role role = RoleLocalServiceUtil.getRole(
			user.getCompanyId(), RoleConstants.CMS_ADMINISTRATOR);

		UserLocalServiceUtil.addRoleUser(role.getRoleId(), user);

		_testPublishCustomObjectDefinition(
			_addCustomObjectDefinition(_adminUser), user);
	}

	@Test
	public void testUpdateCustomObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(),
				" must have UPDATE permission for ",
				"com.liferay.object.model.ObjectDefinition ",
				objectDefinition.getObjectDefinitionId()),
			() -> _testUpdateCustomObjectDefinition(
				objectDefinition, 0, _user1));

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user2.getUserId(),
				" must have ADD_OBJECT_DEFINITION permission for ",
				"com.liferay.object.model.ObjectFolder ",
				_objectFolder.getObjectFolderId()),
			() -> _testUpdateCustomObjectDefinition(
				_addCustomObjectDefinition(_adminUser),
				_objectFolder.getObjectFolderId(), _user2));

		_testUpdateCustomObjectDefinition(
			_addCustomObjectDefinition(_adminUser),
			_objectFolder.getObjectFolderId(), _adminUser);
		_testUpdateCustomObjectDefinition(
			_addCustomObjectDefinition(_user1), 0, _user1);
	}

	@Test
	public void testUpdateSystemObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition = _addSystemObjectDefinition(
			0, _adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(),
				" must have UPDATE permission for ",
				"com.liferay.object.model.ObjectDefinition ",
				objectDefinition.getObjectDefinitionId()),
			() -> _testUpdateSystemObjectDefinition(
				objectDefinition, 0, _user1));

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user2.getUserId(),
				" must have ADD_OBJECT_DEFINITION permission for ",
				"com.liferay.object.model.ObjectFolder ",
				_objectFolder.getObjectFolderId()),
			() -> _testUpdateSystemObjectDefinition(
				_addSystemObjectDefinition(0, _adminUser),
				_objectFolder.getObjectFolderId(), _user2));

		_testUpdateSystemObjectDefinition(
			_addSystemObjectDefinition(0, _adminUser),
			_objectFolder.getObjectFolderId(), _adminUser);
		_testUpdateSystemObjectDefinition(
			_addSystemObjectDefinition(0, _user2), 0, _user2);
	}

	@Test
	public void testUpdateTitleObjectFieldId() throws Exception {
		ObjectDefinition objectDefinition = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(),
				" must have UPDATE permission for ",
				"com.liferay.object.model.ObjectDefinition ",
				objectDefinition.getObjectDefinitionId()),
			() -> _testUpdateTitleObjectFieldId(
				objectDefinition, _adminUser, _user1));

		_testUpdateTitleObjectFieldId(
			_addCustomObjectDefinition(_adminUser), _adminUser, _adminUser);
		_testUpdateTitleObjectFieldId(
			_addCustomObjectDefinition(_user1), _user1, _user1);
	}

	private ObjectDefinition _addCustomObjectDefinition(User user)
		throws Exception {

		// Do not publish the custom object definition to ensure we test that
		// permission resources are added before publishing

		/*ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				user.getUserId(), "Test", null);

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			user.getUserId(), objectDefinition.getObjectDefinitionId());*/

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			null, user.getUserId(), 0, null, false, true, false, true, true,
			false, false, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionTestUtil.getRandomName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_COMPANY,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.emptyList(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())),
			Collections.emptyList());
	}

	private ObjectDefinition _addSystemObjectDefinition(
			long objectFolderId, User user)
		throws Exception {

		_setUser(user);

		return _objectDefinitionService.addSystemObjectDefinition(
			RandomTestUtil.randomString(), user.getUserId(), objectFolderId,
			null, false, true, false, true, false, false, false, false, false,
			null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"Test", null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			false, ObjectDefinitionConstants.SCOPE_COMPANY,
			Collections.emptyList(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())),
			Collections.emptyList());
	}

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private void _setUpCMSContext() throws Exception {
		Group group = _groupLocalService.fetchGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		if (group != null) {
			return;
		}

		group = GroupTestUtil.addGroup();

		group.setGroupKey(GroupConstants.CMS);

		group = _groupLocalService.updateGroup(group);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		try {

			// Manually initialize the CMS site initializer until the feature
			// flag LPD-17564 is removed

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.cms");

			siteInitializer.initialize(group.getGroupId());

			Bundle testBundle = FrameworkUtil.getBundle(
				ObjectDefinitionServiceTest.class);

			BundleContext bundleContext = testBundle.getBundleContext();

			for (Bundle bundle : bundleContext.getBundles()) {
				if (Objects.equals(
						bundle.getSymbolicName(),
						"com.liferay.site.initializer.cms")) {

					_deleteFile(bundle, "00.list.type.definition");
					_deleteFile(bundle, "01.object.folder");
					_deleteFile(bundle, "02.object.definition");

					CompletableFuture<Void> completableFuture =
						_batchEngineUnitProcessor.processBatchEngineUnits(
							_batchEngineUnitReader.getBatchEngineUnits(bundle));

					completableFuture.join();
				}
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private void _setUser(User user) {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	private void _testAddCustomObjectDefinition(long objectFolderId, User user)
		throws Exception {

		ObjectDefinition objectDefinition = null;

		try {
			_setUser(user);

			objectDefinition =
				_objectDefinitionService.addCustomObjectDefinition(
					null, objectFolderId, null, false, true, false, true, true,
					false, false, false, false, null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					ObjectDefinitionTestUtil.getRandomName(), null, null,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					true, ObjectDefinitionConstants.SCOPE_COMPANY,
					ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
					Collections.emptyList(),
					Collections.singletonList(
						ObjectFieldUtil.createObjectField(
							ObjectFieldConstants.BUSINESS_TYPE_TEXT,
							ObjectFieldConstants.DB_TYPE_STRING,
							RandomTestUtil.randomString(),
							StringUtil.randomId())),
					Collections.emptyList());

			objectDefinition =
				_objectDefinitionLocalService.publishCustomObjectDefinition(
					user.getUserId(), objectDefinition.getObjectDefinitionId());
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testAddObjectDefinition(long objectFolderId, User user)
		throws Exception {

		ObjectDefinition objectDefinition = null;

		try {
			_setUser(user);

			objectDefinition = _objectDefinitionService.addObjectDefinition(
				RandomTestUtil.randomString(), objectFolderId, true,
				ObjectDefinitionConstants.SCOPE_COMPANY, false);
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testAddSystemObjectDefinition(long objectFolderId, User user)
		throws Exception {

		ObjectDefinition objectDefinition = null;

		try {
			_setUser(user);

			objectDefinition = _addSystemObjectDefinition(objectFolderId, user);
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testDeleteObjectDefinition(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		ObjectDefinition deleteObjectDefinition = null;

		try {
			_setUser(user);

			deleteObjectDefinition =
				_objectDefinitionService.deleteObjectDefinition(
					objectDefinition.getObjectDefinitionId());
		}
		finally {
			if (deleteObjectDefinition == null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testGetObjectDefinition(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		try {
			_setUser(user);

			_objectDefinitionService.getObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testPublishCustomObjectDefinition(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		try {
			_setUser(user);

			objectDefinition =
				_objectDefinitionService.publishCustomObjectDefinition(
					objectDefinition.getObjectDefinitionId());
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testUpdateCustomObjectDefinition(
			ObjectDefinition objectDefinition, long objectFolderId, User user)
		throws Exception {

		try {
			_setUser(user);

			objectDefinition =
				_objectDefinitionService.updateCustomObjectDefinition(
					null, objectDefinition.getObjectDefinitionId(), 0, 0,
					objectFolderId, 0, false, objectDefinition.isActive(), null,
					true, false, true, false, true, false, false, false, false,
					false, false, null,
					LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
					null, false, LocalizedMapUtil.getLocalizedMap("Ables"),
					objectDefinition.getScope(), objectDefinition.getStatus(),
					Collections.emptyList(), Collections.emptyList(),
					Collections.emptyList());
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testUpdateSystemObjectDefinition(
			ObjectDefinition objectDefinition, long objectFolderId, User user)
		throws Exception {

		try {
			_setUser(user);

			objectDefinition =
				_objectDefinitionService.updateSystemObjectDefinition(
					RandomTestUtil.randomString(),
					objectDefinition.getObjectDefinitionId(), objectFolderId,
					objectDefinition.getTitleObjectFieldId(),
					Collections.emptyList(), Collections.emptyList(),
					Collections.emptyList());
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testUpdateTitleObjectFieldId(
			ObjectDefinition objectDefinition, User ownerUser, User user)
		throws Exception {

		try {
			_setUser(user);

			ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
				new TextObjectFieldBuilder(
				).userId(
					ownerUser.getUserId()
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					StringUtil.randomId()
				).objectDefinitionId(
					objectDefinition.getObjectDefinitionId()
				).build());

			objectDefinition =
				_objectDefinitionService.updateTitleObjectFieldId(
					objectDefinition.getObjectDefinitionId(),
					objectField.getObjectFieldId());
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private User _adminUser;

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionService _objectDefinitionService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@DeleteAfterTestRun
	private ObjectFolder _objectFolder;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

	private User _user1;
	private User _user2;

	@Inject(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}