/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.integration.internal;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Roselaine Marques
 */
public class WorkflowTaskManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_userLocalService = Mockito.mock(UserLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_workflowTaskManagerImpl, "_userLocalService", _userLocalService);

		PermissionThreadLocal.setPermissionChecker(
			_mockPermissionChecker(_USER_ID));
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(null);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAssignWorkflowTaskToUserThrowsExceptionWhenAssigneeUserBelongsToDifferentVirtualInstance()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();
		long assigneeUserId = RandomTestUtil.randomLong();

		User assigneeUser = Mockito.mock(User.class);

		Mockito.when(
			assigneeUser.getCompanyId()
		).thenReturn(
			companyId + 1
		);

		Mockito.when(
			_userLocalService.fetchUser(assigneeUserId)
		).thenReturn(
			assigneeUser
		);

		_workflowTaskManagerImpl.assignWorkflowTaskToUser(
			companyId, _USER_ID, RandomTestUtil.randomLong(), assigneeUserId,
			null, null, null);
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	public void testAssignWorkflowTaskToUserThrowsExceptionWhenAssigneeUserDoesNotExist()
		throws Exception {

		long companyId = RandomTestUtil.randomLong();

		long assigneeUserId = RandomTestUtil.randomLong();

		Mockito.when(
			_userLocalService.fetchUser(assigneeUserId)
		).thenReturn(
			null
		);

		_workflowTaskManagerImpl.assignWorkflowTaskToUser(
			companyId, _USER_ID, RandomTestUtil.randomLong(), assigneeUserId,
			null, null, null);
	}

	private PermissionChecker _mockPermissionChecker(long userId) {
		return new SimplePermissionChecker() {

			@Override
			public long getUserId() {
				return userId;
			}

		};
	}

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private UserLocalService _userLocalService;
	private final WorkflowTaskManagerImpl _workflowTaskManagerImpl =
		new WorkflowTaskManagerImpl();

}