/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_10.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class JournalArticleSmallImageSourceUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgrade() throws Exception {
		_group = GroupTestUtil.addGroup();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		DB db = DBManagerUtil.getDB();

		db.runSQL(
			StringBundler.concat(
				"update JournalArticle set smallImage = [$TRUE$], ",
				"smallImageId = ", RandomTestUtil.nextLong(),
				", smallImageSource = ",
				JournalArticleConstants.SMALL_IMAGE_SOURCE_URL,
				", smallImageURL = '' where id_ = ", journalArticle.getId()));

		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(6, 1, 10));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_multiVMPool.clear();

		journalArticle = _journalArticleLocalService.fetchArticle(
			journalArticle.getId());

		Assert.assertEquals(
			JournalArticleConstants.SMALL_IMAGE_SOURCE_USER_COMPUTER,
			journalArticle.getSmallImageSource());
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.journal.internal.upgrade.registry.JournalServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}