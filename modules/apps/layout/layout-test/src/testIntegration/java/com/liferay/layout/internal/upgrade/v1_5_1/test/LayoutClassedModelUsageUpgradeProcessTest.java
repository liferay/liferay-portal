/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v1_5_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;

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
public class LayoutClassedModelUsageUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_fragmentEntryLinkClassNameId = _classNameLocalService.getClassNameId(
			FragmentEntryLink.class.getName());

		_group = GroupTestUtil.addGroup();

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_journalArticleClassNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class.getName());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = _layout.fetchDraftLayout();
	}

	@Test
	@TestInfo("LPD-53620")
	public void testUpgrade() throws Exception {
		FragmentEntryLink draftFragmentEntryLink = _addFragmentEntryLink();

		_assertLayoutClassedModelUsages(
			1, draftFragmentEntryLink.getFragmentEntryLinkId(),
			_draftLayout.getPlid());

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);

		FragmentEntryLink publishedFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				_group.getGroupId(),
				draftFragmentEntryLink.getFragmentEntryLinkId(),
				_layout.getPlid());

		_assertLayoutClassedModelUsages(
			2, publishedFragmentEntryLink.getFragmentEntryLinkId(),
			_layout.getPlid());

		long deletedDraftFragmentEntryLinkId = RandomTestUtil.randomLong();
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_group.getGroupId(), _journalArticle.getExternalReferenceCode(),
			_journalArticleClassNameId, _journalArticle.getResourcePrimKey(),
			String.valueOf(deletedDraftFragmentEntryLinkId),
			_fragmentEntryLinkClassNameId, _draftLayout.getPlid(),
			serviceContext);

		_assertLayoutClassedModelUsages(
			3, deletedDraftFragmentEntryLinkId, _draftLayout.getPlid());

		long deletedPublishedFragmentEntryLinkId = RandomTestUtil.randomLong();

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			_group.getGroupId(), _journalArticle.getExternalReferenceCode(),
			_journalArticleClassNameId, _journalArticle.getResourcePrimKey(),
			String.valueOf(deletedPublishedFragmentEntryLinkId),
			_fragmentEntryLinkClassNameId, _layout.getPlid(), serviceContext);

		_assertLayoutClassedModelUsages(
			4, deletedPublishedFragmentEntryLinkId, _layout.getPlid());

		runUpgrade();

		_assertLayoutClassedModelUsages(
			2, draftFragmentEntryLink.getFragmentEntryLinkId(),
			_draftLayout.getPlid());
		_assertLayoutClassedModelUsages(
			2, publishedFragmentEntryLink.getFragmentEntryLinkId(),
			_layout.getPlid());

		Assert.assertNull(
			_layoutClassedModelUsageLocalService.fetchLayoutClassedModelUsage(
				_group.getGroupId(), _journalArticle.getExternalReferenceCode(),
				_journalArticleClassNameId,
				_journalArticle.getResourcePrimKey(),
				String.valueOf(deletedDraftFragmentEntryLinkId),
				_fragmentEntryLinkClassNameId, _draftLayout.getPlid()));
		Assert.assertNull(
			_layoutClassedModelUsageLocalService.fetchLayoutClassedModelUsage(
				_group.getGroupId(), _journalArticle.getExternalReferenceCode(),
				_journalArticleClassNameId,
				_journalArticle.getResourcePrimKey(),
				String.valueOf(deletedPublishedFragmentEntryLinkId),
				_fragmentEntryLinkClassNameId, _layout.getPlid()));
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink();

		return _layoutClassedModelUsageLocalService.
			fetchLayoutClassedModelUsage(
				_group.getGroupId(), _journalArticle.getExternalReferenceCode(),
				_journalArticleClassNameId,
				_journalArticle.getResourcePrimKey(),
				String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
				_fragmentEntryLinkClassNameId, fragmentEntryLink.getPlid());
	}

	@Override
	protected CTService<?> getCTService() {
		return _layoutClassedModelUsageLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(1, 5, 1));

		UpgradeProcess upgradeProcess = upgradeProcesses[0];

		upgradeProcess.upgrade();

		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		LayoutClassedModelUsage layoutClassedModelUsage =
			(LayoutClassedModelUsage)ctModel;

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				GetterUtil.getLong(layoutClassedModelUsage.getContainerKey()));

		_fragmentEntryLinkLocalService.updateFragmentEntryLink(
			fragmentEntryLink);

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(_draftLayout.getPlid());

		return layoutClassedModelUsages.get(0);
	}

	private FragmentEntryLink _addFragmentEntryLink() throws Exception {
		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				() -> JSONUtil.put(
					RandomTestUtil.randomString(),
					JSONUtil.put(
						"classNameId",
						String.valueOf(_journalArticleClassNameId)
					).put(
						"classPK",
						String.valueOf(_journalArticle.getResourcePrimKey())
					).put(
						"externalReferenceCode",
						_journalArticle.getExternalReferenceCode()
					))
			).toString(),
			_draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid()));
	}

	private void _assertLayoutClassedModelUsages(
		int expectedCount, long fragmentEntryLinkId, long plid) {

		Assert.assertEquals(
			expectedCount,
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount(
					_journalArticleClassNameId,
					_journalArticle.getResourcePrimKey()));

		LayoutClassedModelUsage layoutClassedModelUsage =
			_layoutClassedModelUsageLocalService.fetchLayoutClassedModelUsage(
				_group.getGroupId(), _journalArticle.getExternalReferenceCode(),
				_journalArticleClassNameId,
				_journalArticle.getResourcePrimKey(),
				String.valueOf(fragmentEntryLinkId),
				_fragmentEntryLinkClassNameId, plid);

		if (expectedCount > 0) {
			Assert.assertNotNull(layoutClassedModelUsage);
		}
		else {
			Assert.assertNull(layoutClassedModelUsage);
		}
	}

	@Inject(
		filter = "(&(component.name=com.liferay.layout.internal.upgrade.registry.LayoutServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Layout _draftLayout;
	private long _fragmentEntryLinkClassNameId;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;
	private long _journalArticleClassNameId;
	private Layout _layout;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}