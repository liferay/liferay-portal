/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v6_1_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.test.util.BaseFriendlyURLFormatUpgradeProcessTestCase;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author João Victor Alves
 */
@RunWith(Arquillian.class)
public class JournalArticleFriendlyURLFormatUpgradeProcessTest
	extends BaseFriendlyURLFormatUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_classNameId = PortalUtil.getClassNameId(
			JournalArticle.class.getName());
	}

	@Test
	public void testUpgradeContainingSlash() throws Exception {
		_addJournalArticle("test/test");

		_runUpgrade();

		_assertURLTitle("test/test");
	}

	@Test
	public void testUpgradeForCapitalSpecialCharacter() throws Exception {
		_addJournalArticle("test001óóóÓ");

		_runUpgrade();

		_assertURLTitle(
			_friendlyURLNormalizer.normalizeWithEncoding("test001óóóó"));
	}

	@Test
	public void testUpgradeKeepsAlreadyNormalizedURLTitleWithDuplicateSibling()
		throws Exception {

		_addJournalArticle("test");

		JournalArticle journalArticle = _journalArticle;

		_addJournalArticle("Test");

		_runUpgrade();

		_assertURLTitle("test-1");

		_journalArticle = journalArticle;

		_assertURLTitle("test");
	}

	@Test
	public void testUpgradeWithDuplicateFriendlyURL() throws Exception {
		_addJournalArticle("test");
		_addJournalArticle("test/");

		_runUpgrade();

		_assertURLTitle("test-1");
	}

	@Test
	public void testUpgradeWithMultipleLocales() throws Exception {
		String languageId = LocaleUtil.toLanguageId(LocaleUtil.SPAIN);

		_addJournalArticle("english/");

		updateFriendlyURLLocalization(
			_classNameId, _journalArticle.getResourcePrimKey(), languageId,
			"spanish/");

		_runUpgrade();

		_assertURLTitle("english");

		_assertFriendlyURLEntryLocalization(languageId, "spanish");
	}

	@Test
	public void testUpgradeWithMultipleTrailingSlashes() throws Exception {
		_addJournalArticle("test///");

		_runUpgrade();

		_assertURLTitle("test");
	}

	@Test
	public void testUpgradeWithTrailingSlash() throws Exception {
		_addJournalArticle("test/");

		_runUpgrade();

		_assertURLTitle("test");
	}

	private void _addJournalArticle(String urlTitle) {
		_journalArticle = _journalArticleLocalService.createJournalArticle(
			counterLocalService.increment(JournalArticle.class.getName()));

		_journalArticle.setResourcePrimKey(_journalArticle.getPrimaryKey());
		_journalArticle.setGroupId(group.getGroupId());
		_journalArticle.setUrlTitle(urlTitle);
		_journalArticle.setDefaultLanguageId(defaultLanguageId);

		_journalArticleLocalService.addJournalArticle(_journalArticle);

		_journalArticleResourceLocalService.addJournalArticleResource(
			_journalArticleResourceLocalService.createJournalArticleResource(
				_journalArticle.getResourcePrimKey()));

		addFriendlyURLEntry(_journalArticle.getResourcePrimKey(), _classNameId);

		updateFriendlyURLLocalization(
			_classNameId, _journalArticle.getResourcePrimKey(),
			defaultLanguageId, urlTitle);
	}

	private void _assertFriendlyURLEntryLocalization(
		String languageId, String urlTitle) {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				group.getGroupId(), _classNameId,
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				languageId, urlTitle);

		Assert.assertEquals(
			urlTitle, friendlyURLEntryLocalization.getUrlTitle());
	}

	private void _assertURLTitle(String urlTitle) {
		_journalArticle = _journalArticleLocalService.fetchJournalArticle(
			_journalArticle.getPrimaryKey());

		Assert.assertEquals(urlTitle, _journalArticle.getUrlTitle());

		_assertFriendlyURLEntryLocalization(defaultLanguageId, urlTitle);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.journal.internal.upgrade.v6_1_3." +
			"JournalArticleFriendlyURLFormatUpgradeProcess";

	private long _classNameId;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.journal.internal.upgrade.registry.JournalServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}