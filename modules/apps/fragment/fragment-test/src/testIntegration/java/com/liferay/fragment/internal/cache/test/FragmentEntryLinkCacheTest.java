/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.cache.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.cache.FragmentEntryLinkCache;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-53565")
	public void test() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout = layout.fetchDraftLayout();

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), draftLayout.getGroupId(), 0,
				StringUtil.randomString(), StringUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), true, "{fieldSets: []}", null, 0,
				false, false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					draftLayout.getGroupId(), TestPropsValues.getUserId()));

		FragmentEntryLink draftLayoutFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				"{}", fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getExternalReferenceCode(),
				fragmentEntry.getScopeERC(), fragmentEntry.getHtml(),
				fragmentEntry.getJs(), draftLayout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				null, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				layout.getGroupId(),
				draftLayoutFragmentEntryLink.getExternalReferenceCode(),
				layout.getPlid());

		Locale locale = _portal.getSiteDefaultLocale(_group);

		Assert.assertNull(
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, locale));

		String content = RandomTestUtil.randomString();

		_fragmentEntryLinkCache.putFragmentEntryLinkContent(
			content, fragmentEntryLink, locale);

		Assert.assertEquals(
			content,
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, locale));

		_fragmentEntryLinkCache.removeFragmentEntryLinkCache(fragmentEntryLink);

		Assert.assertNull(
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, locale));

		_fragmentEntryLinkCache.putFragmentEntryLinkContent(
			content, fragmentEntryLink, locale);

		Assert.assertEquals(
			content,
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, locale));

		_fragmentEntryLinkCache.removeFragmentEntryLinkCache(
			fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertNull(
			_fragmentEntryLinkCache.getFragmentEntryLinkContent(
				fragmentEntryLink, locale));

		long randomLong = RandomTestUtil.randomLong();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.fragment.internal.cache." +
					"FragmentEntryLinkCacheImpl",
				LoggerTestUtil.DEBUG)) {

			_fragmentEntryLinkCache.removeFragmentEntryLinkCache(randomLong);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"No fragment entry link exists with the fragment entry link " +
					"ID " + randomLong,
				logEntry.getMessage());
		}
	}

	@Inject
	private FragmentEntryLinkCache _fragmentEntryLinkCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}