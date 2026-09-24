/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectActionService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class ObjectActionServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_guestUser = _userLocalService.getGuestUser(
			TestPropsValues.getCompanyId());
		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
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
	public void testAddObjectAction() throws Exception {
		try {
			_testAddObjectAction(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testAddObjectAction(_user);
	}

	@Test
	public void testDeleteObjectAction() throws Exception {
		try {
			_testDeleteObjectAction(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testDeleteObjectAction(_user);
	}

	@Test
	public void testGetObjectAction() throws Exception {
		try {
			_testGetObjectAction(_guestUser);
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have VIEW permission for"));
		}

		_testGetObjectAction(_user);
	}

	@Test
	public void testUpdateObjectAction() throws Exception {
		try {
			_testUpdateObjectAction(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testUpdateObjectAction(_user);
	}

	private ObjectAction _addObjectAction(User user) throws Exception {
		return _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), user.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			UnicodePropertiesBuilder.put(
				"url", RandomTestUtil.randomString()
			).build(),
			false);
	}

	private void _setUser(User user) {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	private void _testAddObjectAction(User user) throws Exception {
		ObjectAction objectAction = null;

		try {
			_setUser(user);

			objectAction = _objectActionService.addObjectAction(
				RandomTestUtil.randomString(),
				_objectDefinition.getObjectDefinitionId(), true,
				StringPool.BLANK,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_WEBHOOK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
				UnicodePropertiesBuilder.put(
					"url", RandomTestUtil.randomString()
				).build(),
				false);
		}
		finally {
			if (objectAction != null) {
				_objectActionLocalService.deleteObjectAction(objectAction);
			}
		}
	}

	private void _testDeleteObjectAction(User user) throws Exception {
		ObjectAction deleteObjectAction = null;
		ObjectAction objectAction = null;

		try {
			_setUser(user);

			objectAction = _addObjectAction(user);

			deleteObjectAction = _objectActionService.deleteObjectAction(
				objectAction.getObjectActionId());
		}
		finally {
			if (deleteObjectAction == null) {
				_objectActionLocalService.deleteObjectAction(objectAction);
			}
		}
	}

	private void _testGetObjectAction(User user) throws Exception {
		ObjectAction objectAction = null;

		try {
			_setUser(user);

			objectAction = _addObjectAction(user);

			_objectActionService.getObjectAction(
				objectAction.getObjectActionId());
		}
		finally {
			if (objectAction != null) {
				_objectActionLocalService.deleteObjectAction(objectAction);
			}
		}
	}

	private void _testUpdateObjectAction(User user) throws Exception {
		ObjectAction objectAction = null;

		try {
			_setUser(user);

			objectAction = _addObjectAction(user);

			objectAction = _objectActionService.updateObjectAction(
				RandomTestUtil.randomString(), objectAction.getObjectActionId(),
				true, StringPool.BLANK,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				RandomTestUtil.randomString(),
				ObjectActionExecutorConstants.KEY_WEBHOOK,
				ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE,
				UnicodePropertiesBuilder.put(
					"secret", "standalone"
				).put(
					"url", "https://standalone.com"
				).build());
		}
		finally {
			if (objectAction != null) {
				_objectActionLocalService.deleteObjectAction(objectAction);
			}
		}
	}

	private User _guestUser;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@Inject
	private ObjectActionService _objectActionService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;
	private User _user;

	@Inject(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}