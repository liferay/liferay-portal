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
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.definition.util.ObjectDefinitionUtil;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;
import com.liferay.site.cms.site.initializer.util.RoleUtil;

import java.util.Arrays;
import java.util.Collections;

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
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_adminUser = TestPropsValues.getUser();
		_objectFolder = _objectFolderLocalService.addObjectFolder(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString());
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAddCustomObjectDefinition() throws Exception {
		_testAddObjectDefinition(
			(objectFolderId, user) -> _testAddCustomObjectDefinition(
				objectFolderId, ObjectDefinitionConstants.SCOPE_COMPANY, user));
	}

	@Test
	@TestInfo("LPD-66895")
	public void testAddCustomObjectDefinitionByCMSAdministratorRole()
		throws Exception {

		CMSTestUtil.getOrAddGroup(ObjectDefinitionServiceTest.class);

		User user = _addCMSAdministratorUser();

		_testAddCustomObjectDefinition(
			0, ObjectDefinitionConstants.SCOPE_DEPOT, user);

		ObjectFolder objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				user.getCompanyId());

		_testAddCustomObjectDefinition(
			objectFolder.getObjectFolderId(),
			ObjectDefinitionConstants.SCOPE_DEPOT, user);

		objectFolder =
			_objectFolderLocalService.getObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				user.getCompanyId());

		_testAddCustomObjectDefinition(
			objectFolder.getObjectFolderId(),
			ObjectDefinitionConstants.SCOPE_DEPOT, user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(),
				" must have ADD_OBJECT_DEFINITION permission for ",
				"com.liferay.object.model.ObjectFolder ",
				_objectFolder.getObjectFolderId()),
			() -> _testAddCustomObjectDefinition(
				_objectFolder.getObjectFolderId(),
				ObjectDefinitionConstants.SCOPE_DEPOT, user));
	}

	@Test
	public void testAddSystemObjectDefinition() throws Exception {
		_testAddObjectDefinition(this::_testAddSystemObjectDefinition);
	}

	@Test
	public void testDeleteObjectDefinition() throws Exception {

		// Can delete object definition with individual model permission

		ObjectDefinition objectDefinition1 = _addCustomObjectDefinition(
			_adminUser);

		_setResourcePermissions(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition1.getObjectDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.DELETE});

		_testDeleteObjectDefinition(objectDefinition1, _user);

		// Can delete object definition with model permission

		_setResourcePermissions(
			ObjectDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ActionKeys.DELETE});

		_testDeleteObjectDefinition(
			_addCustomObjectDefinition(_adminUser), _user);

		_removeResourcePermission(
			ObjectDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ActionKeys.DELETE);

		// Can delete object definition with owner permission

		_testDeleteObjectDefinition(_addCustomObjectDefinition(_user), _user);

		// Cannot delete object definition without model permission

		ObjectDefinition objectDefinition2 = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.DELETE,
				" permission for ", ObjectDefinition.class.getName(),
				StringPool.SPACE, objectDefinition2.getObjectDefinitionId()),
			() -> _testDeleteObjectDefinition(objectDefinition2, _user));

		// Cannot delete object definition without owner permission

		ObjectDefinition objectDefinition3 = _addCustomObjectDefinition(_user);

		_removeResourcePermission(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition3.getObjectDefinitionId()),
			RoleConstants.OWNER, ActionKeys.DELETE);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.DELETE,
				" permission for ", ObjectDefinition.class.getName(),
				StringPool.SPACE, objectDefinition3.getObjectDefinitionId()),
			() -> _testDeleteObjectDefinition(objectDefinition3, _user));
	}

	@Test
	public void testGetObjectDefinition() throws Exception {

		// Can get object definition with individual model permission

		ObjectDefinition objectDefinition1 = _addCustomObjectDefinition(
			_adminUser);

		_setResourcePermissions(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition1.getObjectDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.VIEW});

		_testGetObjectDefinition(objectDefinition1, _user);

		// Can get object definition with model permission

		_setResourcePermissions(
			ObjectDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ActionKeys.VIEW});

		_testGetObjectDefinition(_addCustomObjectDefinition(_adminUser), _user);

		_removeResourcePermission(
			ObjectDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ActionKeys.VIEW);

		// Can get object definition with owner permission

		_testGetObjectDefinition(_addCustomObjectDefinition(_user), _user);

		// Cannot get object definition without model permission

		ObjectDefinition objectDefinition2 = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.VIEW,
				" permission for ", ObjectDefinition.class.getName(),
				StringPool.SPACE, objectDefinition2.getObjectDefinitionId()),
			() -> _testGetObjectDefinition(objectDefinition2, _user));

		// Cannot get object definition without owner permission

		ObjectDefinition objectDefinition3 = _addCustomObjectDefinition(_user);

		_removeResourcePermission(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition3.getObjectDefinitionId()),
			RoleConstants.OWNER, ActionKeys.VIEW);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.VIEW,
				" permission for ", ObjectDefinition.class.getName(),
				StringPool.SPACE, objectDefinition3.getObjectDefinitionId()),
			() -> _testGetObjectDefinition(objectDefinition3, _user));
	}

	@Test
	public void testPublishCustomObjectDefinition() throws Exception {

		// Can publish custom object definition with permission

		_setResourcePermissions(
			ObjectConstants.RESOURCE_NAME, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ObjectActionKeys.PUBLISH_OBJECT_DEFINITION});

		_testPublishCustomObjectDefinition(
			_addCustomObjectDefinition(_adminUser), _user);

		// Cannot publish custom object definition without permission

		_removeResourcePermission(
			ObjectConstants.RESOURCE_NAME, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ObjectActionKeys.PUBLISH_OBJECT_DEFINITION);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ",
				ObjectActionKeys.PUBLISH_OBJECT_DEFINITION, " permission for ",
				ObjectConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _testPublishCustomObjectDefinition(
				_addCustomObjectDefinition(_adminUser), _user));
	}

	@Test
	@TestInfo("LPD-66895")
	public void testPublishCustomObjectDefinitionByCMSAdministratorRole()
		throws Exception {

		CMSTestUtil.getOrAddGroup(ObjectDefinitionServiceTest.class);

		_testPublishCustomObjectDefinition(
			_addCustomObjectDefinition(_adminUser), _addCMSAdministratorUser());
	}

	@Test
	public void testUpdateCustomObjectDefinition() throws Exception {
		_testUpdateObjectDefinition(this::_testUpdateCustomObjectDefinition);
	}

	@Test
	public void testUpdateSystemObjectDefinition() throws Exception {
		_testUpdateObjectDefinition(this::_testUpdateSystemObjectDefinition);
	}

	@Test
	public void testUpdateTitleObjectFieldId() throws Exception {

		// Can update title object field ID with individual model permission

		ObjectDefinition objectDefinition1 = _addCustomObjectDefinition(
			_adminUser);

		_setResourcePermissions(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition1.getObjectDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.UPDATE});

		_testUpdateTitleObjectFieldId(objectDefinition1, _user);

		// Can update title object field ID with model permission

		_setResourcePermissions(
			ObjectDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ActionKeys.UPDATE});

		_testUpdateTitleObjectFieldId(
			_addCustomObjectDefinition(_adminUser), _user);

		_removeResourcePermission(
			ObjectDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ActionKeys.UPDATE);

		// Can update title object field ID with owner permission

		_testUpdateTitleObjectFieldId(_addCustomObjectDefinition(_user), _user);

		// Cannot update title object field ID without model permission

		ObjectDefinition objectDefinition2 = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.UPDATE,
				" permission for ", ObjectDefinition.class.getName(),
				StringPool.SPACE, objectDefinition2.getObjectDefinitionId()),
			() -> _testUpdateTitleObjectFieldId(objectDefinition2, _user));

		// Cannot update title object field ID without owner permission

		ObjectDefinition objectDefinition3 = _addCustomObjectDefinition(_user);

		_removeResourcePermission(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition3.getObjectDefinitionId()),
			RoleConstants.OWNER, ActionKeys.UPDATE);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.UPDATE,
				" permission for ", ObjectDefinition.class.getName(),
				StringPool.SPACE, objectDefinition3.getObjectDefinitionId()),
			() -> _testUpdateTitleObjectFieldId(objectDefinition3, _user));
	}

	private User _addCMSAdministratorUser() throws Exception {
		User user = UserTestUtil.addUser();

		Role role = RoleUtil.getOrAddCMSAdministratorRole(
			user.getCompanyId(), user.getUserId());

		UserLocalServiceUtil.addRoleUser(role.getRoleId(), user);

		return user;
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

		return ObjectDefinitionTestUtil.addCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(), user.getUserId());
	}

	private long _getRoleId(String roleName) throws Exception {
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		return role.getRoleId();
	}

	private void _removeResourcePermission(
			String name, int scope, String primKey, String roleName,
			String actionId)
		throws Exception {

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(), name, scope, primKey,
			_getRoleId(roleName), actionId);
	}

	private void _setResourcePermissions(
			String name, int scope, String primKey, String roleName,
			String[] actionIds)
		throws Exception {

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), name, scope, primKey,
			_getRoleId(roleName), actionIds);
	}

	private void _testAddCustomObjectDefinition(
			long objectFolderId, String scope, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			ObjectDefinition objectDefinition =
				_objectDefinitionService.addCustomObjectDefinition(
					null, objectFolderId, null, true, false, true, false, true,
					false, false, false, false, null,
					RandomTestUtil.randomLocaleStringMap(),
					ObjectDefinitionTestUtil.getRandomName(), null, null,
					RandomTestUtil.randomLocaleStringMap(), true, scope,
					ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
					Collections.emptyList(),
					Collections.singletonList(
						ObjectFieldUtil.createObjectField(
							ObjectFieldConstants.BUSINESS_TYPE_TEXT,
							ObjectFieldConstants.DB_TYPE_STRING,
							RandomTestUtil.randomString(),
							StringUtil.randomId())),
					Collections.emptyList(), new ServiceContext());

			_objectDefinitionLocalService.publishCustomObjectDefinition(
				user.getUserId(), objectDefinition.getObjectDefinitionId());
		}
	}

	private void _testAddObjectDefinition(
			UnsafeBiConsumer<Long, User, Exception> unsafeBiConsumer)
		throws Exception {

		// Can add object definition with permission

		_setResourcePermissions(
			ObjectConstants.RESOURCE_NAME, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ObjectActionKeys.ADD_OBJECT_DEFINITION});

		unsafeBiConsumer.accept(0L, _user);

		_setResourcePermissions(
			ObjectFolder.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ObjectActionKeys.ADD_OBJECT_DEFINITION});

		unsafeBiConsumer.accept(_objectFolder.getObjectFolderId(), _user);

		// Cannot add object definition without permission

		_removeResourcePermission(
			ObjectFolder.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ",
				ObjectActionKeys.ADD_OBJECT_DEFINITION, " permission for ",
				ObjectFolder.class.getName(), StringPool.SPACE,
				_objectFolder.getObjectFolderId()),
			() -> unsafeBiConsumer.accept(
				_objectFolder.getObjectFolderId(), _user));

		_removeResourcePermission(
			ObjectConstants.RESOURCE_NAME, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ",
				ObjectActionKeys.ADD_OBJECT_DEFINITION, " permission for ",
				ObjectConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> unsafeBiConsumer.accept(0L, _user));
	}

	private void _testAddSystemObjectDefinition(long objectFolderId, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_objectDefinitionService.addSystemObjectDefinition(
				RandomTestUtil.randomString(), user.getUserId(), objectFolderId,
				ObjectDefinitionUtil.generateRandomClassName(), true, false,
				true, false, true, false, false, false, false, false, null,
				RandomTestUtil.randomLocaleStringMap(),
				"Test" + RandomTestUtil.randomString(), null, null,
				RandomTestUtil.randomLocaleStringMap(), false,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList());
		}
	}

	private void _testDeleteObjectDefinition(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_objectDefinitionService.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}
	}

	private void _testGetObjectDefinition(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_objectDefinitionService.getObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}
	}

	private void _testPublishCustomObjectDefinition(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_objectDefinitionService.publishCustomObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}
	}

	private void _testUpdateCustomObjectDefinition(
			ObjectDefinition objectDefinition, long objectFolderId, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_objectDefinitionService.updateCustomObjectDefinition(
				null, objectDefinition.getObjectDefinitionId(), 0, 0,
				objectFolderId, 0, false, objectDefinition.isActive(), null,
				true, false, true, false, true, false, false, false, false,
				false, null, RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionTestUtil.getRandomName(), null, null, false,
				RandomTestUtil.randomLocaleStringMap(),
				objectDefinition.getScope(), objectDefinition.getStatus(),
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), new ServiceContext());
		}
	}

	private void _testUpdateObjectDefinition(
			UnsafeTriConsumer<ObjectDefinition, Long, User, Exception>
				unsafeTriConsumer)
		throws Exception {

		// Can update custom object definition with individual model permission

		ObjectDefinition objectDefinition1 = _addCustomObjectDefinition(
			_adminUser);

		_setResourcePermissions(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition1.getObjectDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.UPDATE});

		unsafeTriConsumer.accept(objectDefinition1, 0L, _user);

		_setResourcePermissions(
			ObjectFolder.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_objectFolder.getObjectFolderId()),
			RoleConstants.USER,
			new String[] {ObjectActionKeys.ADD_OBJECT_DEFINITION});

		unsafeTriConsumer.accept(
			objectDefinition1, _objectFolder.getObjectFolderId(), _user);

		_removeResourcePermission(
			ObjectFolder.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_objectFolder.getObjectFolderId()),
			RoleConstants.USER, ObjectActionKeys.ADD_OBJECT_DEFINITION);

		// Can update custom object definition with model permission

		_setResourcePermissions(
			ObjectDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ActionKeys.UPDATE});

		unsafeTriConsumer.accept(
			_addCustomObjectDefinition(_adminUser), 0L, _user);

		_setResourcePermissions(
			ObjectFolder.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			new String[] {ObjectActionKeys.ADD_OBJECT_DEFINITION});

		unsafeTriConsumer.accept(
			_addCustomObjectDefinition(_adminUser),
			_objectFolder.getObjectFolderId(), _user);

		_removeResourcePermission(
			ObjectDefinition.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ActionKeys.UPDATE);
		_removeResourcePermission(
			ObjectFolder.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), RoleConstants.USER,
			ObjectActionKeys.ADD_OBJECT_DEFINITION);

		// Can update custom object definition with owner permission

		unsafeTriConsumer.accept(_addCustomObjectDefinition(_user), 0L, _user);

		// Cannot update custom object definition without model permission

		ObjectDefinition objectDefinition2 = _addCustomObjectDefinition(
			_adminUser);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.UPDATE,
				" permission for ", ObjectDefinition.class.getName(),
				StringPool.SPACE, objectDefinition2.getObjectDefinitionId()),
			() -> unsafeTriConsumer.accept(objectDefinition2, 0L, _user));

		_setResourcePermissions(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition2.getObjectDefinitionId()),
			RoleConstants.USER, new String[] {ActionKeys.UPDATE});

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ",
				ObjectActionKeys.ADD_OBJECT_DEFINITION, " permission for ",
				ObjectFolder.class.getName(), StringPool.SPACE,
				_objectFolder.getObjectFolderId()),
			() -> unsafeTriConsumer.accept(
				objectDefinition2, _objectFolder.getObjectFolderId(), _user));

		// Cannot update custom object definition without owner permission

		ObjectDefinition objectDefinition3 = _addCustomObjectDefinition(_user);

		_removeResourcePermission(
			ObjectDefinition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectDefinition3.getObjectDefinitionId()),
			RoleConstants.OWNER, ActionKeys.UPDATE);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", _user.getUserId(), " must have ", ActionKeys.UPDATE,
				" permission for ", ObjectDefinition.class.getName(),
				StringPool.SPACE, objectDefinition3.getObjectDefinitionId()),
			() -> unsafeTriConsumer.accept(objectDefinition3, 0L, _user));
	}

	private void _testUpdateSystemObjectDefinition(
			ObjectDefinition objectDefinition, long objectFolderId, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_objectDefinitionService.updateSystemObjectDefinition(
				RandomTestUtil.randomString(),
				objectDefinition.getObjectDefinitionId(), objectFolderId,
				objectDefinition.getTitleObjectFieldId(),
				Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());
		}
	}

	private void _testUpdateTitleObjectFieldId(
			ObjectDefinition objectDefinition, User user)
		throws Exception {

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				StringUtil.randomId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).userId(
				objectDefinition.getUserId()
			).build());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			_objectDefinitionService.updateTitleObjectFieldId(
				objectDefinition.getObjectDefinitionId(),
				objectField.getObjectFieldId());
		}
	}

	private User _adminUser;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionService _objectDefinitionService;

	@DeleteAfterTestRun
	private ObjectFolder _objectFolder;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	private User _user;

}