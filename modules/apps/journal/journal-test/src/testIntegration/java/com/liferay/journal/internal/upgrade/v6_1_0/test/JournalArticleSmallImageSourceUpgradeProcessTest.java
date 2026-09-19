/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
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
 * @author Manuel Rives
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

		JournalArticle journalArticleWithoutSmallImage =
			_addJournalArticleWithoutSmallImage();
		JournalArticle journalArticleWithSmallImageFromURL =
			_addJournalArticleWithSmallImageFromURL();
		JournalArticle journalArticleWithSmallImageFromUserComputer =
			_addJournalArticleWithSmallImageFromUserComputer();

		_runUpgrade();

		journalArticleWithoutSmallImage =
			_journalArticleLocalService.fetchArticle(
				journalArticleWithoutSmallImage.getId());

		Assert.assertEquals(
			JournalArticleConstants.SMALL_IMAGE_SOURCE_NONE,
			journalArticleWithoutSmallImage.getSmallImageSource());

		journalArticleWithSmallImageFromURL =
			_journalArticleLocalService.fetchArticle(
				journalArticleWithSmallImageFromURL.getId());

		Assert.assertEquals(
			JournalArticleConstants.SMALL_IMAGE_SOURCE_URL,
			journalArticleWithSmallImageFromURL.getSmallImageSource());

		journalArticleWithSmallImageFromUserComputer =
			_journalArticleLocalService.fetchArticle(
				journalArticleWithSmallImageFromUserComputer.getId());

		Assert.assertEquals(
			JournalArticleConstants.SMALL_IMAGE_SOURCE_USER_COMPUTER,
			journalArticleWithSmallImageFromUserComputer.getSmallImageSource());
	}

	private JournalArticle _addJournalArticleWithSmallImageFromURL()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		journalArticle.setSmallImage(true);
		journalArticle.setSmallImageId(_counterLocalService.increment());
		journalArticle.setSmallImageURL(RandomTestUtil.randomString());

		return _journalArticleLocalService.updateJournalArticle(journalArticle);
	}

	private JournalArticle _addJournalArticleWithSmallImageFromUserComputer()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		journalArticle.setSmallImage(true);
		journalArticle.setSmallImageId(RandomTestUtil.nextLong());

		return _journalArticleLocalService.updateJournalArticle(journalArticle);
	}

	private JournalArticle _addJournalArticleWithoutSmallImage()
		throws Exception {

		return JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.journal.internal.upgrade.v6_1_0." +
			"JournalArticleSmallImageSourceUpgradeProcess";

	@Inject
	private CounterLocalService _counterLocalService;

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