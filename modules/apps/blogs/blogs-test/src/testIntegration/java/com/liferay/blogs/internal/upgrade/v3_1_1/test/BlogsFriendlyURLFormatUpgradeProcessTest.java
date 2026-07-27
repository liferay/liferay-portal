/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.upgrade.v3_1_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.test.util.BaseFriendlyURLFormatUpgradeProcessTestCase;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
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
public class BlogsFriendlyURLFormatUpgradeProcessTest
	extends BaseFriendlyURLFormatUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_classNameId = PortalUtil.getClassNameId(BlogsEntry.class.getName());
	}

	@Test
	public void testUpgradeContainingSlash() throws Exception {
		_addBlogsEntry("test/test");

		_runUpgrade();

		_assertURLTitle("test/test");
	}

	@Test
	public void testUpgradeForCapitalSpecialCharacter() throws Exception {
		_addBlogsEntry("test001óóóÓ");

		_runUpgrade();

		_assertURLTitle(
			_friendlyURLNormalizer.normalizeWithEncoding("test001óóóó"));
	}

	@Test
	public void testUpgradeKeepsAlreadyNormalizedURLTitleWithDuplicateSibling()
		throws Exception {

		_addBlogsEntry("test");

		BlogsEntry blogsEntry = _blogsEntry;

		_addBlogsEntry("Test");

		_runUpgrade();

		_assertURLTitle("test-1");

		_blogsEntry = blogsEntry;

		_assertURLTitle("test");
	}

	@Test
	public void testUpgradeWithDuplicateFriendlyURL() throws Exception {
		_addBlogsEntry("test");
		_addBlogsEntry("test/");

		_runUpgrade();

		_assertURLTitle("test-1");
	}

	@Test
	public void testUpgradeWithMultipleTrailingSlashes() throws Exception {
		_addBlogsEntry("test///");

		_runUpgrade();

		_assertURLTitle("test");
	}

	@Test
	public void testUpgradeWithTrailingSlash() throws Exception {
		_addBlogsEntry("test/");

		_runUpgrade();

		_assertURLTitle("test");
	}

	private void _addBlogsEntry(String urlTitle) {
		_blogsEntry = _blogsEntryLocalService.createBlogsEntry(
			counterLocalService.increment(BlogsEntry.class.getName()));

		_blogsEntry.setGroupId(group.getGroupId());
		_blogsEntry.setUrlTitle(urlTitle);
		_blogsEntry.setContent("test");

		_blogsEntryLocalService.addBlogsEntry(_blogsEntry);

		addFriendlyURLEntry(_blogsEntry.getEntryId(), _classNameId);

		updateFriendlyURLLocalization(
			_classNameId, _blogsEntry.getEntryId(), defaultLanguageId,
			urlTitle);
	}

	private void _assertURLTitle(String urlTitle) {
		_blogsEntry = _blogsEntryLocalService.fetchBlogsEntry(
			_blogsEntry.getEntryId());

		Assert.assertEquals(urlTitle, _blogsEntry.getUrlTitle());

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				group.getGroupId(), _classNameId,
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				_blogsEntry.getUrlTitle());

		Assert.assertEquals(
			urlTitle, friendlyURLEntryLocalization.getUrlTitle());
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
		"com.liferay.blogs.internal.upgrade.v3_1_1." +
			"BlogsFriendlyURLFormatUpgradeProcess";

	private BlogsEntry _blogsEntry;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	private long _classNameId;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.blogs.internal.upgrade.registry.BlogsServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}