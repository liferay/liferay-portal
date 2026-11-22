/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.app.local.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class DLAppLocalServiceWhenDeletingAFolderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_folder = DLAppLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@Test
	public void testShouldDeleteSubscriptions() throws Exception {
		DLAppLocalServiceUtil.subscribeFolder(
			TestPropsValues.getUserId(), _group.getGroupId(),
			_folder.getFolderId());

		Assert.assertTrue(
			SubscriptionLocalServiceUtil.isSubscribed(
				_group.getCompanyId(), TestPropsValues.getUserId(),
				DLFolderConstants.getClassName(), _folder.getFolderId()));

		DLAppLocalServiceUtil.deleteFolder(_folder.getFolderId());

		Assert.assertFalse(
			SubscriptionLocalServiceUtil.isSubscribed(
				_group.getCompanyId(), TestPropsValues.getUserId(),
				DLFolderConstants.getClassName(), _folder.getFolderId()));
	}

	private Folder _folder;

	@DeleteAfterTestRun
	private Group _group;

}