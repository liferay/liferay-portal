/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.comment.internal;

import com.liferay.message.boards.service.MBBanLocalService;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class MBDiscussionPermissionImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_mbDiscussionPermissionImpl, "_mbBanLocalService",
			Mockito.mock(MBBanLocalService.class));
	}

	@After
	public void tearDown() {
		_modelResourcePermissionUtilMockedStatic.close();
		_resourceActionsUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-115808")
	public void testHasPermissionWhenModelResourcePermissionIsMissing() {
		_modelResourcePermissionUtilMockedStatic.when(
			() -> ModelResourcePermissionUtil.contains(
				Mockito.eq(_permissionChecker), Mockito.eq(_GROUP_ID),
				Mockito.eq(_CLASS_NAME), Mockito.eq(_CLASS_PK),
				Mockito.anyString())
		).thenReturn(
			null
		);

		Mockito.when(
			_permissionChecker.hasPermission(
				Mockito.eq(_GROUP_ID), Mockito.eq(_CLASS_NAME),
				Mockito.eq(_CLASS_PK), Mockito.anyString())
		).thenReturn(
			true
		);

		Assert.assertFalse(_hasPermission(ActionKeys.ADD_DISCUSSION));
		Assert.assertFalse(_hasPermission(ActionKeys.DELETE_DISCUSSION));
		Assert.assertFalse(_hasPermission(ActionKeys.UPDATE_DISCUSSION));
		Assert.assertFalse(_hasPermission(ActionKeys.VIEW));
	}

	@Test
	@TestInfo("LPD-115808")
	public void testHasPermissionWhenModelResourcePermissionIsPresent() {
		_mockModelResourcePermissionUtil(ActionKeys.UPDATE, false);
		_mockModelResourcePermissionUtil(ActionKeys.VIEW, true);

		Assert.assertTrue(_hasPermission(ActionKeys.ADD_DISCUSSION));
		Assert.assertFalse(_hasPermission(ActionKeys.DELETE_DISCUSSION));
		Assert.assertFalse(_hasPermission(ActionKeys.UPDATE_DISCUSSION));
		Assert.assertTrue(_hasPermission(ActionKeys.VIEW));
	}

	private boolean _hasPermission(String actionId) {
		return _mbDiscussionPermissionImpl.hasPermission(
			_permissionChecker, RandomTestUtil.randomLong(), _GROUP_ID,
			_CLASS_NAME, _CLASS_PK, actionId);
	}

	private void _mockModelResourcePermissionUtil(
		String actionId, boolean permission) {

		_modelResourcePermissionUtilMockedStatic.when(
			() -> ModelResourcePermissionUtil.contains(
				_permissionChecker, _GROUP_ID, _CLASS_NAME, _CLASS_PK, actionId)
		).thenReturn(
			permission
		);
	}

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final long _CLASS_PK = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final MBDiscussionPermissionImpl _mbDiscussionPermissionImpl =
		new MBDiscussionPermissionImpl();
	private final MockedStatic<ModelResourcePermissionUtil>
		_modelResourcePermissionUtilMockedStatic = Mockito.mockStatic(
			ModelResourcePermissionUtil.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final MockedStatic<ResourceActionsUtil>
		_resourceActionsUtilMockedStatic = Mockito.mockStatic(
			ResourceActionsUtil.class);

}