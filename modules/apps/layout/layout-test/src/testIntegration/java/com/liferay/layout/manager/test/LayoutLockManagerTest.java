/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.model.LockedLayout;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.exception.LockedLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class LayoutLockManagerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetLock() throws Exception {
		Layout draftLayout = _getDraftLayout();

		_lockLayout(draftLayout, _user);

		Lock lock = _lockManager.fetchLock(
			Layout.class.getName(), draftLayout.getPlid());

		Assert.assertNotNull(lock);
	}

	@Test(expected = LockedLayoutException.class)
	public void testGetLockWithDifferentUser() throws Exception {
		Layout draftLayout = _getDraftLayout();

		User newUser = UserTestUtil.addUser();

		try {
			_lockLayout(draftLayout, _user);
			_lockLayout(draftLayout, newUser);
		}
		finally {
			_userLocalService.deleteUser(newUser);
		}
	}

	@Test
	public void testGetLockWithSameUser() throws Exception {
		long originalLockExpirationTime =
			ReflectionTestUtil.getAndSetFieldValue(
				_layoutLockManager, "_lockExpirationTime", 60000L);

		Layout draftLayout = _getDraftLayout();

		try {
			_lockLayout(draftLayout, _user);

			Lock lock1 = _lockManager.fetchLock(
				Layout.class.getName(), draftLayout.getPlid());

			Assert.assertNotNull(lock1);
			Assert.assertNotNull(lock1.getExpirationDate());

			_lockLayout(draftLayout, _user);

			Lock lock2 = _lockManager.fetchLock(
				Layout.class.getName(), draftLayout.getPlid());

			Assert.assertNotNull(lock2);
			Assert.assertNotNull(lock2.getExpirationDate());

			Assert.assertEquals(lock1.getLockId(), lock2.getLockId());

			Date lock1ExpirationDate = lock1.getExpirationDate();
			Date lock2ExpirationDate = lock2.getExpirationDate();

			Assert.assertTrue(lock2ExpirationDate.after(lock1ExpirationDate));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_layoutLockManager, "_lockExpirationTime",
				originalLockExpirationTime);
		}
	}

	@Test
	public void testGetLockedLayouts() throws Exception {
		long[] layoutPlids = {};

		Layout draftLayout1 = _getDraftLayout();

		layoutPlids = ArrayUtil.append(layoutPlids, draftLayout1.getPlid());

		_lockLayout(draftLayout1, _user);

		Layout draftLayout2 = _getDraftLayout();

		layoutPlids = ArrayUtil.append(layoutPlids, draftLayout2.getPlid());

		_lockLayout(draftLayout2, _user);

		LayoutTestUtil.addTypePortletLayout(_group);
		LayoutTestUtil.addTypeContentLayout(_group);

		List<LockedLayout> lockedLayouts = _layoutLockManager.getLockedLayouts(
			TestPropsValues.getCompanyId(), _group.getGroupId(),
			LocaleUtil.getDefault());

		Assert.assertEquals(lockedLayouts.toString(), 2, lockedLayouts.size());

		for (LockedLayout lockedLayout : lockedLayouts) {
			Assert.assertTrue(
				ArrayUtil.contains(layoutPlids, lockedLayout.getPlid()));
		}
	}

	@Test
	public void testGetLockedLayoutsWithDifferentGroups() throws Exception {
		Layout draftLayout = _getDraftLayout(_group);

		_lockLayout(draftLayout, _user);

		_lockLayout(_getDraftLayout(GroupTestUtil.addGroup()), _user);

		List<LockedLayout> lockedLayouts = _layoutLockManager.getLockedLayouts(
			TestPropsValues.getCompanyId(), _group.getGroupId(),
			LocaleUtil.getDefault());

		Assert.assertEquals(lockedLayouts.toString(), 1, lockedLayouts.size());

		LockedLayout lockedLayout = lockedLayouts.get(0);

		Assert.assertEquals(draftLayout.getPlid(), lockedLayout.getPlid());
	}

	@Test
	public void testUnlock() throws Exception {
		Layout draftLayout = _getDraftLayout();

		_lockLayout(draftLayout, _user);

		Lock lock = _lockManager.fetchLock(
			Layout.class.getName(), draftLayout.getPlid());

		Assert.assertNotNull(lock);

		_layoutLockManager.unlock(draftLayout, _user.getUserId());

		lock = _lockManager.fetchLock(
			Layout.class.getName(), draftLayout.getPlid());

		Assert.assertNull(lock);
	}

	private Layout _getDraftLayout() throws Exception {
		return _getDraftLayout(_group);
	}

	private Layout _getDraftLayout(Group group) throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		Layout draftLayout = layout.fetchDraftLayout();

		draftLayout.setStatus(WorkflowConstants.STATUS_DRAFT);

		return _layoutLocalService.updateLayout(draftLayout);
	}

	private void _lockLayout(Layout layout, User user) throws PortalException {
		MockActionRequest mockActionRequest = new MockActionRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLayout(layout);
		themeDisplay.setUser(user);

		mockActionRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		_layoutLockManager.getLock(mockActionRequest);
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutLockManager _layoutLockManager;

	@Inject
	private LockManager _lockManager;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}