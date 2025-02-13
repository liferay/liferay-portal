/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
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
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ObjectDefinitionServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

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
	public void testUpdateRootObjectDefinitionId() throws Exception {
		ObjectDefinition objectDefinition = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user1.getUserId(),
				" must have UPDATE permission for ",
				"com.liferay.object.model.ObjectDefinition ",
				objectDefinition.getObjectDefinitionId()),
			() -> _testUpdateRootObjectDefinitionId(objectDefinition, _user1));

		_testUpdateRootObjectDefinitionId(
			_addCustomObjectDefinition(_adminUser), _adminUser);
		_testUpdateRootObjectDefinitionId(
			_addCustomObjectDefinition(_user1), _user1);
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
			user.getUserId(), 0, null, false, false, true, false, false,
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
					RandomTestUtil.randomString(), StringUtil.randomId())));
	}

	private ObjectDefinition _addSystemObjectDefinition(
			long objectFolderId, User user)
		throws Exception {

		_setUser(user);

		return _objectDefinitionService.addSystemObjectDefinition(
			RandomTestUtil.randomString(), user.getUserId(), objectFolderId,
			false, false, true, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			"Test", null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			false, ObjectDefinitionConstants.SCOPE_COMPANY,
			Collections.emptyList(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING,
					RandomTestUtil.randomString(), StringUtil.randomId())));
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
					objectFolderId, null, false, false, true, false, false,
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
							StringUtil.randomId())));

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
					true, false, false, true, false, false, false,
					LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
					null, false, LocalizedMapUtil.getLocalizedMap("Ables"),
					objectDefinition.getScope(), objectDefinition.getStatus(),
					Collections.emptyList());
		}
		finally {
			if (objectDefinition != null) {
				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	private void _testUpdateRootObjectDefinitionId(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		try {
			_setUser(user);

			objectDefinition =
				_objectDefinitionService.updateRootObjectDefinitionId(
					objectDefinition.getObjectDefinitionId(),
					objectDefinition.getObjectDefinitionId());
		}
		finally {
			if (objectDefinition != null) {
				objectDefinition =
					_objectDefinitionService.updateRootObjectDefinitionId(
						objectDefinition.getObjectDefinitionId(), 0);

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
	private User _user1;
	private User _user2;

	@Inject(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}