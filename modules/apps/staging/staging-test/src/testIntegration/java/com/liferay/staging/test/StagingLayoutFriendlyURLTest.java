/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.friendly.url.LayoutFriendlyURLEntryHelper;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.LayoutFriendlyURLsException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@RunWith(Arquillian.class)
public class StagingLayoutFriendlyURLTest extends BaseLocalStagingTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_languageId = liveGroup.getDefaultLanguageId();

		_locale = LocaleUtil.fromLanguageId(_languageId);
	}

	@Test
	public void testLayoutFriendlyURL() throws Exception {
		_testLayoutFriendlyURL();
	}

	private void _assertPublishLayoutsWithLayoutFriendlyURLsException(
			Layout layout)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			publishLayouts(new long[] {layout.getLayoutId()});

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertTrue(
				String.valueOf(throwable),
				throwable instanceof LayoutFriendlyURLsException);
		}
	}

	private void _deleteFriendlyURLEntryLocalization(
			String friendlyURL, long groupId)
		throws Exception {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				groupId, _layoutFriendlyURLEntryHelper.getClassNameId(false),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				friendlyURL);

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryLocalization.getFriendlyURLEntryId(),
			friendlyURLEntryLocalization.getLanguageId());
	}

	private String _randomFriendlyURL() {
		String urlTitle = StringUtil.toLowerCase(
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE));

		return StringPool.FORWARD_SLASH + urlTitle;
	}

	private void _testLayoutFriendlyURL() throws Exception {
		String originalFriendlyURL = stagingLayout.getFriendlyURL();

		String testFriendlyURL = _randomFriendlyURL();

		stagingLayout = LayoutTestUtil.updateFriendlyURL(
			stagingLayout,
			HashMapBuilder.put(
				_locale, testFriendlyURL
			).build());

		Layout stagingLayout2 = LayoutTestUtil.addTypePortletLayout(
			stagingGroup);

		String originalLayout2FriendlyURL = stagingLayout2.getFriendlyURL();

		publishLayouts();

		stagingLayout = LayoutTestUtil.updateFriendlyURL(
			stagingLayout,
			HashMapBuilder.put(
				_locale, originalFriendlyURL
			).build());

		long layoutClassNameId = _layoutFriendlyURLEntryHelper.getClassNameId(
			false);

		_deleteFriendlyURLEntryLocalization(
			testFriendlyURL, stagingGroup.getGroupId());

		stagingLayout2 = LayoutTestUtil.updateFriendlyURL(
			stagingLayout2,
			HashMapBuilder.put(
				_locale, testFriendlyURL
			).build());

		_assertPublishLayoutsWithLayoutFriendlyURLsException(stagingLayout2);

		Layout liveLayout2 = _layoutLocalService.getLayoutByUuidAndGroupId(
			stagingLayout2.getUuid(), liveGroup.getGroupId(),
			stagingLayout2.isPrivateLayout());

		Assert.assertEquals(
			originalLayout2FriendlyURL, liveLayout2.getFriendlyURL(_locale));

		_assertPublishLayoutsWithLayoutFriendlyURLsException(stagingLayout2);

		List<BackgroundTask> failedBackgroundTasks =
			_backgroundTaskLocalService.getBackgroundTasks(
				stagingGroup.getGroupId(),
				"com.liferay.exportimport.internal.background.task." +
					"LayoutStagingBackgroundTaskExecutor",
				BackgroundTaskConstants.STATUS_FAILED);

		Assert.assertTrue(
			"There should not be a failed staging publication",
			failedBackgroundTasks.isEmpty());

		publishLayouts(new long[] {stagingLayout.getLayoutId()});

		Assert.assertEquals(
			originalFriendlyURL, liveLayout.getFriendlyURL(_locale));

		_deleteFriendlyURLEntryLocalization(
			testFriendlyURL, liveGroup.getGroupId());

		publishLayouts();

		Assert.assertEquals(
			originalFriendlyURL, liveLayout.getFriendlyURL(_locale));
		Assert.assertEquals(
			testFriendlyURL, liveLayout2.getFriendlyURL(_locale));

		FriendlyURLEntryLocalization liveFriendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				liveGroup.getGroupId(), layoutClassNameId,
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				_languageId, originalFriendlyURL);

		Assert.assertEquals(
			liveLayout.getPlid(),
			liveFriendlyURLEntryLocalization.getClassPK());

		liveFriendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				liveGroup.getGroupId(), layoutClassNameId,
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				_languageId, testFriendlyURL);

		Assert.assertEquals(
			liveLayout2.getPlid(),
			liveFriendlyURLEntryLocalization.getClassPK());
	}

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	private String _languageId;

	@Inject
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private Locale _locale;

}