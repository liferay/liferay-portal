/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.upgrade.v1_4_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalContentSearchLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
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
public class LayoutClassedModelUsageCTUpgradeProcessTest
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
			JournalArticle.class);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_draftLayout = layout.fetchDraftLayout();
	}

	@Test
	@TestInfo("LPD-56394")
	public void testNoIndexDuplicationWhenFragmentUpdatedInMultiplePublications()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			_journalArticle);

		_deleteLayoutClassedModelUsage(_journalArticle.getResourcePrimKey());

		_updateFragmentEntryLinkInNewCTCollection(
			fragmentEntryLink, _journalArticle);

		_updateFragmentEntryLinkInNewCTCollection(
			fragmentEntryLink, _journalArticle);

		runUpgrade();
	}

	@Test
	public void testUpgrade() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			_journalArticle);

		_deleteLayoutClassedModelUsage(_journalArticle.getResourcePrimKey());

		runUpgrade();

		_assertLayoutClassedModelUsages(
			_journalArticle.getResourcePrimKey(), 1, fragmentEntryLink);
	}

	@Test
	public void testUpgradeExistingUsages() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			_journalArticle);

		_assertLayoutClassedModelUsages(
			_journalArticle.getResourcePrimKey(), 1, fragmentEntryLink);

		runUpgrade();

		_assertLayoutClassedModelUsages(
			_journalArticle.getResourcePrimKey(), 1, fragmentEntryLink);
	}

	@Test
	public void testUpgradeMultipleEditableFields() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			_journalArticle, journalArticle);

		_deleteLayoutClassedModelUsage(
			_journalArticle.getResourcePrimKey(),
			journalArticle.getResourcePrimKey());

		runUpgrade();

		_assertLayoutClassedModelUsages(
			_journalArticle.getResourcePrimKey(), 1, fragmentEntryLink);
		_assertLayoutClassedModelUsages(
			journalArticle.getResourcePrimKey(), 1, fragmentEntryLink);
	}

	@Test
	public void testUpgradeSameContentMappedInMultipleEditables()
		throws Exception {

		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			_journalArticle, _journalArticle);

		_deleteLayoutClassedModelUsage(_journalArticle.getResourcePrimKey());

		runUpgrade();

		_assertLayoutClassedModelUsages(
			_journalArticle.getResourcePrimKey(), 1, fragmentEntryLink);
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		FragmentEntryLink fragmentEntryLink = _addFragmentEntryLink(
			_journalArticle);

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
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
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

	private FragmentEntryLink _addFragmentEntryLink(
			JournalArticle... journalArticles)
		throws Exception {

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				() -> {
					JSONObject jsonObject = _jsonFactory.createJSONObject();

					for (JournalArticle journalArticle : journalArticles) {
						jsonObject.put(
							RandomTestUtil.randomString(),
							JSONUtil.put(
								"classNameId",
								String.valueOf(_journalArticleClassNameId)
							).put(
								"classPK",
								String.valueOf(
									journalArticle.getResourcePrimKey())
							).put(
								"externalReferenceCode",
								_journalArticle.getExternalReferenceCode()
							));
					}

					return jsonObject;
				}
			).toString(),
			_draftLayout,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid()));
	}

	private void _assertLayoutClassedModelUsages(
		long classPK, int expectedCount, FragmentEntryLink fragmentEntryLink) {

		Assert.assertEquals(
			expectedCount,
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesCount(
					_journalArticleClassNameId, classPK));

		LayoutClassedModelUsage layoutClassedModelUsage =
			_layoutClassedModelUsageLocalService.fetchLayoutClassedModelUsage(
				fragmentEntryLink.getGroupId(),
				_journalArticle.getExternalReferenceCode(),
				_journalArticleClassNameId, classPK,
				String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
				_fragmentEntryLinkClassNameId, fragmentEntryLink.getPlid());

		if (expectedCount > 0) {
			Assert.assertNotNull(layoutClassedModelUsage);
		}
		else {
			Assert.assertNull(layoutClassedModelUsage);
		}
	}

	private void _deleteLayoutClassedModelUsage(long... classPKs) {
		for (long classPK : classPKs) {
			List<LayoutClassedModelUsage> layoutClassedModelUsages =
				_layoutClassedModelUsageLocalService.
					getLayoutClassedModelUsages(
						_journalArticleClassNameId, classPK);

			Assert.assertEquals(
				layoutClassedModelUsages.toString(), 1,
				layoutClassedModelUsages.size());

			_layoutClassedModelUsageLocalService.deleteLayoutClassedModelUsage(
				layoutClassedModelUsages.get(0));

			layoutClassedModelUsages =
				_layoutClassedModelUsageLocalService.
					getLayoutClassedModelUsages(
						_journalArticleClassNameId, classPK);

			Assert.assertEquals(
				layoutClassedModelUsages.toString(), 0,
				layoutClassedModelUsages.size());
		}
	}

	private void _updateFragmentEntryLinkInNewCTCollection(
			FragmentEntryLink fragmentEntryLink, JournalArticle journalArticle)
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollection.getCtCollectionId())) {

			_fragmentEntryLinkLocalService.updateFragmentEntryLink(
				fragmentEntryLink);
			_deleteLayoutClassedModelUsage(journalArticle.getResourcePrimKey());
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.layout.internal.upgrade.v1_4_1." +
			"LayoutClassedModelUsageUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.layout.internal.upgrade.registry.LayoutServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	private Layout _draftLayout;
	private long _fragmentEntryLinkClassNameId;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;
	private long _journalArticleClassNameId;

	@Inject
	private JournalContentSearchLocalService _journalContentSearchLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}