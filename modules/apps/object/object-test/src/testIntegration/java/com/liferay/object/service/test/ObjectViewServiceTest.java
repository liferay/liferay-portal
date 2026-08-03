/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectView;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectViewLocalService;
import com.liferay.object.service.ObjectViewService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ObjectViewServiceTest {

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
	public void testAddObjectView() throws Exception {
		try {
			_testAddObjectView(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testAddObjectView(_user);
	}

	@Test
	public void testDeleteObjectView() throws Exception {
		try {
			_testDeleteObjectView(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have DELETE permission for"));
		}

		_testDeleteObjectView(_user);
	}

	@Test
	public void testGetObjectView() throws Exception {
		try {
			_testGetObjectView(_guestUser);
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have VIEW permission for"));
		}

		_testGetObjectView(_user);
	}

	@Test
	public void testUpdateObjectView() throws Exception {
		try {
			_testUpdateObjectView(_guestUser);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			String message = principalException.getMessage();

			Assert.assertTrue(
				message.contains(
					"User " + _guestUser.getUserId() +
						" must have UPDATE permission for"));
		}

		_testUpdateObjectView(_user);
	}

	private ObjectView _addObjectView(User user) throws Exception {
		return _objectViewLocalService.addObjectView(
			null, user.getUserId(), _objectDefinition.getObjectDefinitionId(),
			false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList());
	}

	private void _setUser(User user) {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	private void _testAddObjectView(User user) throws Exception {
		_setUser(user);

		ObjectView objectView = _objectViewService.addObjectView(
			null, _objectDefinition.getObjectDefinitionId(), false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList());

		_objectViewLocalService.deleteObjectView(objectView);
	}

	private void _testDeleteObjectView(User user) throws Exception {
		_setUser(user);

		ObjectView objectView = _addObjectView(user);

		_objectViewService.deleteObjectView(objectView.getObjectViewId());

		_objectViewLocalService.deleteObjectView(objectView);
	}

	private void _testGetObjectView(User user) throws Exception {
		_setUser(user);

		ObjectView objectView = _addObjectView(user);

		_objectViewService.getObjectView(objectView.getObjectViewId());

		_objectViewLocalService.deleteObjectView(objectView);
	}

	private void _testUpdateObjectView(User user) throws Exception {
		_setUser(user);

		ObjectView objectView = _addObjectView(user);

		objectView = _objectViewService.updateObjectView(
			null, objectView.getObjectViewId(), false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			Collections.emptyList(), Collections.emptyList(),
			Collections.emptyList());

		_objectViewLocalService.deleteObjectView(objectView);
	}

	private User _guestUser;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectViewLocalService _objectViewLocalService;

	@Inject
	private ObjectViewService _objectViewService;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;
	private User _user;

	@Inject(type = UserLocalService.class)
	private UserLocalService _userLocalService;

}