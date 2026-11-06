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
import com.liferay.portal.kernel.exception.LayoutFriendlyURLsException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
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
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_languageId = liveGroup.getDefaultLanguageId();

		_locale = LocaleUtil.fromLanguageId(_languageId);
	}

	@Test
	@TestInfo("LPD-113926")
	public void testLayoutFriendlyURL() throws Exception {
		_testLayoutFriendlyURLWithReusedFriendlyURLCombinedPublish();
		_testLayoutFriendlyURLWithReusedFriendlyURLIndividualPublish();
		_testLayoutFriendlyURLWithSwappedFriendlyURLs();
	}

	private void _assertPublishLayoutsWithLayoutFriendlyURLsException(
			int expectedLogEntriesCount, long... layoutIds)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			publishLayouts(layoutIds);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			for (LogEntry logEntry : logEntries) {
				Throwable throwable = logEntry.getThrowable();

				Assert.assertTrue(
					throwable instanceof LayoutFriendlyURLsException);
			}

			Assert.assertEquals(
				logEntries.toString(), expectedLogEntriesCount,
				logEntries.size());
		}
	}

	private void _deleteFriendlyURLEntryLocalization(String friendlyURL)
		throws Exception {

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				stagingGroup.getGroupId(),
				_layoutFriendlyURLEntryHelper.getClassNameId(false),
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				friendlyURL);

		_friendlyURLEntryLocalService.deleteFriendlyURLLocalizationEntry(
			friendlyURLEntryLocalization.getFriendlyURLEntryId(),
			friendlyURLEntryLocalization.getLanguageId());
	}

	private Layout _fetchLiveLayout(Layout layout) {
		return _layoutLocalService.fetchLayoutByUuidAndGroupId(
			layout.getUuid(), liveGroup.getGroupId(), layout.isPrivateLayout());
	}

	private String _getRandomFriendlyURL() {
		String urlTitle = StringUtil.toLowerCase(
			RandomTestUtil.randomString(
				LayoutFriendlyURLRandomizerBumper.INSTANCE));

		return StringPool.FORWARD_SLASH + urlTitle;
	}

	private void _testLayoutFriendlyURLWithReusedFriendlyURLCombinedPublish()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(stagingGroup);

		String friendlyURL = layout1.getFriendlyURL(_locale);

		publishLayouts(new long[] {layout1.getLayoutId()});

		layout1 = _updateFriendlyURL(_getRandomFriendlyURL(), layout1);

		_deleteFriendlyURLEntryLocalization(friendlyURL);

		Layout layout2 = LayoutTestUtil.addTypePortletLayout(stagingGroup);

		layout2 = _updateFriendlyURL(friendlyURL, layout2);

		_layoutLocalService.updatePriority(
			layout2.getPlid(), layout1.getPriority());

		_assertPublishLayoutsWithLayoutFriendlyURLsException(
			1, layout1.getLayoutId(), layout2.getLayoutId());

		Assert.assertNull(_fetchLiveLayout(layout2));

		publishLayouts(
			new long[] {layout1.getLayoutId(), layout2.getLayoutId()});

		Layout liveLayout2 = _fetchLiveLayout(layout2);

		Assert.assertEquals(friendlyURL, liveLayout2.getFriendlyURL(_locale));
	}

	private void _testLayoutFriendlyURLWithReusedFriendlyURLIndividualPublish()
		throws Exception {

		String originalFriendlyURL1 = stagingLayout.getFriendlyURL();

		String friendlyURL = _getRandomFriendlyURL();

		stagingLayout = LayoutTestUtil.updateFriendlyURL(
			stagingLayout,
			HashMapBuilder.put(
				_locale, friendlyURL
			).build());

		Layout stagingLayout2 = LayoutTestUtil.addTypePortletLayout(
			stagingGroup);

		String originalFriendlyURL2 = stagingLayout2.getFriendlyURL();

		publishLayouts();

		stagingLayout = LayoutTestUtil.updateFriendlyURL(
			stagingLayout,
			HashMapBuilder.put(
				_locale, originalFriendlyURL1
			).build());

		long layoutClassNameId = _layoutFriendlyURLEntryHelper.getClassNameId(
			false);

		_deleteFriendlyURLEntryLocalization(friendlyURL);

		stagingLayout2 = LayoutTestUtil.updateFriendlyURL(
			stagingLayout2,
			HashMapBuilder.put(
				_locale, friendlyURL
			).build());

		_assertPublishLayoutsWithLayoutFriendlyURLsException(
			1, stagingLayout2.getLayoutId());

		Layout liveLayout2 = _layoutLocalService.getLayoutByUuidAndGroupId(
			stagingLayout2.getUuid(), liveGroup.getGroupId(),
			stagingLayout2.isPrivateLayout());

		Assert.assertEquals(
			originalFriendlyURL2, liveLayout2.getFriendlyURL(_locale));

		_assertPublishLayoutsWithLayoutFriendlyURLsException(
			1, stagingLayout2.getLayoutId());

		publishLayouts(new long[] {stagingLayout.getLayoutId()});

		Assert.assertEquals(
			originalFriendlyURL1, liveLayout.getFriendlyURL(_locale));

		publishLayouts();

		Assert.assertEquals(
			originalFriendlyURL1, liveLayout.getFriendlyURL(_locale));
		Assert.assertEquals(friendlyURL, liveLayout2.getFriendlyURL(_locale));

		FriendlyURLEntryLocalization liveFriendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				liveGroup.getGroupId(), layoutClassNameId,
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				_languageId, originalFriendlyURL1);

		Assert.assertEquals(
			liveLayout.getPlid(),
			liveFriendlyURLEntryLocalization.getClassPK());

		liveFriendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntryLocalization(
				liveGroup.getGroupId(), layoutClassNameId,
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				_languageId, friendlyURL);

		Assert.assertEquals(
			liveLayout2.getPlid(),
			liveFriendlyURLEntryLocalization.getClassPK());
	}

	private void _testLayoutFriendlyURLWithSwappedFriendlyURLs()
		throws Exception {

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(stagingGroup);
		Layout layout2 = LayoutTestUtil.addTypePortletLayout(stagingGroup);

		String friendlyURL1 = layout1.getFriendlyURL(_locale);
		String friendlyURL2 = layout2.getFriendlyURL(_locale);

		publishLayouts(
			new long[] {layout1.getLayoutId(), layout2.getLayoutId()});

		layout1 = _updateFriendlyURL(_getRandomFriendlyURL(), layout1);

		_deleteFriendlyURLEntryLocalization(friendlyURL1);

		layout2 = _updateFriendlyURL(friendlyURL1, layout2);

		_deleteFriendlyURLEntryLocalization(friendlyURL2);

		layout1 = _updateFriendlyURL(friendlyURL2, layout1);

		_assertPublishLayoutsWithLayoutFriendlyURLsException(
			2, layout1.getLayoutId(), layout2.getLayoutId());

		Layout liveLayout1 = _fetchLiveLayout(layout1);

		Assert.assertEquals(friendlyURL1, liveLayout1.getFriendlyURL(_locale));

		Layout liveLayout2 = _fetchLiveLayout(layout2);

		Assert.assertEquals(friendlyURL2, liveLayout2.getFriendlyURL(_locale));
	}

	private Layout _updateFriendlyURL(String friendlyURL, Layout layout)
		throws Exception {

		return LayoutTestUtil.updateFriendlyURL(
			layout,
			HashMapBuilder.put(
				_locale, friendlyURL
			).build());
	}

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	private String _languageId;

	@Inject
	private LayoutFriendlyURLEntryHelper _layoutFriendlyURLEntryHelper;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private Locale _locale;

}