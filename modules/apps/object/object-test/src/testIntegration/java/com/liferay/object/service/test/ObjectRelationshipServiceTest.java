/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.ObjectRelationshipService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectRelationshipServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());

		_objectDefinition1 =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false, LocalizedMapUtil.getLocalizedMap("Able"), "Able", null,
				null, LocalizedMapUtil.getLocalizedMap("Ables"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		_objectDefinition1 =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId());

		_objectDefinition2 =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false, LocalizedMapUtil.getLocalizedMap("Baker"), "Baker", null,
				null, LocalizedMapUtil.getLocalizedMap("Bakers"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		_objectDefinition2 =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_objectDefinition2.getObjectDefinitionId());

		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_user = TestPropsValues.getUser();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testAddObjectRelationship() throws Exception {
		try {
			_testAddObjectRelationship(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testAddObjectRelationship(_user);
	}

	@Test
	public void testAddObjectRelationshipMappingTableValues() throws Exception {
		_testAddObjectRelationshipMappingTableValues(
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);
		_testAddObjectRelationshipMappingTableValues(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);
	}

	@Test
	public void testDeleteObjectRelationship() throws Exception {
		try {
			_testDeleteObjectRelationship(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testDeleteObjectRelationship(_user);
	}

	@Test
	public void testGetObjectRelationship() throws Exception {
		try {
			_testGetObjectRelationship(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have VIEW permission for"));
		}

		_testGetObjectRelationship(_user);
	}

	@Test
	public void testGetObjectRelationships() throws Exception {
		try {
			_testGetObjectRelationships(_guestUser);
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have VIEW permission for"));
		}

		_testGetObjectRelationships(_user);
	}

	@Test
	public void testUpdateObjectRelationship() throws Exception {
		try {
			_testUpdateObjectRelationship(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testUpdateObjectRelationship(_user);
	}

	private ObjectRelationship _addObjectRelationship(User user)
		throws Exception {

		return _objectRelationshipLocalService.addObjectRelationship(
			null, user.getUserId(), _objectDefinition1.getObjectDefinitionId(),
			_objectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);
	}

	private void _setUser(User user) {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	private void _testAddObjectRelationship(User user) throws Exception {
		ObjectRelationship objectRelationship = null;

		try {
			_setUser(user);

			objectRelationship =
				_objectRelationshipService.addObjectRelationship(
					null, _objectDefinition1.getObjectDefinitionId(),
					_objectDefinition2.getObjectDefinitionId(), 0,
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					StringUtil.randomId(), false,
					ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);
		}
		finally {
			if (objectRelationship != null) {
				_objectRelationshipLocalService.deleteObjectRelationship(
					objectRelationship);
			}
		}
	}

	private void _testAddObjectRelationshipMappingTableValues(String type)
		throws Exception {

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false, type, null);

		ObjectEntry objectEntry1 = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			objectDefinition1.getObjectDefinitionId(), null,
			Collections.emptyMap(), ServiceContextTestUtil.getServiceContext());
		ObjectEntry objectEntry2 = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			objectDefinition2.getObjectDefinitionId(), null,
			Collections.emptyMap(), ServiceContextTestUtil.getServiceContext());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		_setUser(user);

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have UPDATE permission for ",
				objectDefinition2.getClassName(), StringPool.SPACE,
				objectEntry2.getObjectEntryId()),
			() ->
				_objectRelationshipService.
					addObjectRelationshipMappingTableValues(
						objectRelationship.getObjectRelationshipId(),
						objectEntry1.getObjectEntryId(),
						objectEntry2.getObjectEntryId(), new ServiceContext()));

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition2.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE});

		if (Objects.equals(
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, type)) {

			_objectRelationshipService.addObjectRelationshipMappingTableValues(
				objectRelationship.getObjectRelationshipId(),
				objectEntry1.getObjectEntryId(),
				objectEntry2.getObjectEntryId(), new ServiceContext());

			return;
		}

		AssertUtils.assertFailure(
			PrincipalException.MustHavePermission.class,
			StringBundler.concat(
				"User ", user.getUserId(), " must have UPDATE permission for ",
				objectDefinition1.getClassName(), StringPool.SPACE,
				objectEntry1.getObjectEntryId()),
			() ->
				_objectRelationshipService.
					addObjectRelationshipMappingTableValues(
						objectRelationship.getObjectRelationshipId(),
						objectEntry1.getObjectEntryId(),
						objectEntry2.getObjectEntryId(), new ServiceContext()));

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition1.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE});

		_objectRelationshipService.addObjectRelationshipMappingTableValues(
			objectRelationship.getObjectRelationshipId(),
			objectEntry1.getObjectEntryId(), objectEntry2.getObjectEntryId(),
			new ServiceContext());
	}

	private void _testDeleteObjectRelationship(User user) throws Exception {
		ObjectRelationship deleteObjectRelationship = null;
		ObjectRelationship objectRelationship = null;

		try {
			_setUser(user);

			objectRelationship = _addObjectRelationship(user);

			deleteObjectRelationship =
				_objectRelationshipService.deleteObjectRelationship(
					objectRelationship.getObjectRelationshipId());
		}
		finally {
			if (deleteObjectRelationship == null) {
				_objectRelationshipLocalService.deleteObjectRelationship(
					objectRelationship);
			}
		}
	}

	private void _testGetObjectRelationship(User user) throws Exception {
		ObjectRelationship objectRelationship = null;

		try {
			_setUser(user);

			objectRelationship = _addObjectRelationship(user);

			_objectRelationshipService.getObjectRelationship(
				objectRelationship.getObjectRelationshipId());
		}
		finally {
			if (objectRelationship != null) {
				_objectRelationshipLocalService.deleteObjectRelationship(
					objectRelationship);
			}
		}
	}

	private void _testGetObjectRelationships(User user) throws Exception {
		ObjectRelationship objectRelationship = null;

		try {
			_setUser(user);

			objectRelationship = _addObjectRelationship(user);

			_objectRelationshipService.getObjectRelationships(
				objectRelationship.getObjectDefinitionId1(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);
		}
		finally {
			if (objectRelationship != null) {
				_objectRelationshipLocalService.deleteObjectRelationship(
					objectRelationship);
			}
		}
	}

	private void _testUpdateObjectRelationship(User user) throws Exception {
		ObjectRelationship objectRelationship = null;

		try {
			_setUser(user);

			objectRelationship = _addObjectRelationship(user);

			objectRelationship =
				_objectRelationshipService.updateObjectRelationship(
					objectRelationship.getExternalReferenceCode(),
					objectRelationship.getObjectRelationshipId(), 0,
					objectRelationship.getDeletionType(), false,
					LocalizedMapUtil.getLocalizedMap("Baker"), null);
		}
		finally {
			if (objectRelationship != null) {
				_objectRelationshipLocalService.deleteObjectRelationship(
					objectRelationship);
			}
		}
	}

	private User _guestUser;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ObjectRelationshipService _objectRelationshipService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private User _user;

	@Inject(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}