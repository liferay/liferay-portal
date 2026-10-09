/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchResourceException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.test.constants.ServiceTestConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.security.permission.DoAsUserThread;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 */
@RunWith(Arquillian.class)
public class ResourceLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddResourcesConcurrently() throws Exception {
		_group = GroupTestUtil.addGroup();

		_users = new User[ServiceTestConstants.THREAD_COUNT];

		for (int i = 0; i < ServiceTestConstants.THREAD_COUNT; i++) {
			User user = UserTestUtil.addUser(
				"ResourceLocalServiceTest" + (i + 1), _group.getGroupId());

			_users[i] = user;
		}

		DoAsUserThread[] doAsUserThreads =
			new DoAsUserThread[ServiceTestConstants.THREAD_COUNT];

		for (int i = 0; i < doAsUserThreads.length; i++) {
			doAsUserThreads[i] = new AddResources(_users[i].getUserId());
		}

		for (DoAsUserThread doAsUserThread : doAsUserThreads) {
			doAsUserThread.start();
		}

		for (DoAsUserThread doAsUserThread : doAsUserThreads) {
			doAsUserThread.join();
		}

		int successCount = 0;

		for (DoAsUserThread doAsUserThread : doAsUserThreads) {
			if (!doAsUserThread.isSuccess()) {
				continue;
			}

			successCount++;
		}

		Assert.assertTrue(
			StringBundler.concat(
				"Only ", successCount, " out of ",
				ServiceTestConstants.THREAD_COUNT,
				" threads added resources successfully"),
			successCount == ServiceTestConstants.THREAD_COUNT);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceLocalServiceTest.class);

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private ResourceLocalService _resourceLocalService;

	@DeleteAfterTestRun
	private User[] _users;

	private class AddResources extends DoAsUserThread {

		@Override
		protected void doRun() throws Exception {
			try {
				_resourceLocalService.getResource(
					TestPropsValues.getCompanyId(), Layout.class.getName(),
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(
						TestPropsValues.getPlid(_group.getGroupId())));
			}
			catch (NoSuchResourceException noSuchResourceException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchResourceException);
				}

				_resourceLocalService.addResources(
					TestPropsValues.getCompanyId(), _group.getGroupId(), 0,
					Layout.class.getName(),
					TestPropsValues.getPlid(_group.getGroupId()), false, true,
					true);
			}
		}

		private AddResources(long userId) {
			super(userId);
		}

	}

}