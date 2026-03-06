/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.data.cleanup.DataCleanup;
import com.liferay.data.cleanup.util.DataCleanupUtil;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class LayoutClassedModelUsageOrphanDataUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo({"LPD-60259", "LPD-62154"})
	public void testUpgrade() throws Exception {
		_setUp();

		_assertLayoutClassedModelUsages(
			0, _journalArticle.getResourcePrimKey(), _draftLayout.getPlid(),
			_layout.getPlid());

		_updateLayoutClassedModelUsages(
			0, _draftLayout.getPlid(), _layout.getPlid());

		_runUpgrade(
			() -> _assertLayoutClassedModelUsages(
				0, _journalArticle.getResourcePrimKey(), _draftLayout.getPlid(),
				_layout.getPlid()));

		_updateLayoutClassedModelUsages(0);

		_runUpgrade(
			() -> _assertLayoutClassedModelUsages(
				0, _journalArticle.getResourcePrimKey(), _draftLayout.getPlid(),
				_layout.getPlid()));
	}

	@Test
	@TestInfo("LPD-70786")
	public void testUpgradeWithCTCollection() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			CTCollection ctCollection = _ctCollectionService.addCTCollection(
				null, 0, RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						ctCollection.getCtCollectionId())) {

				_setUp();

				_assertLayoutClassedModelUsages(
					ctCollection.getCtCollectionId(),
					_journalArticle.getResourcePrimKey(),
					_draftLayout.getPlid(), _layout.getPlid());

				_updateLayoutClassedModelUsages(
					ctCollection.getCtCollectionId());

				_runUpgrade(
					() -> _assertLayoutClassedModelUsages(
						ctCollection.getCtCollectionId(),
						_journalArticle.getResourcePrimKey(),
						_draftLayout.getPlid(), _layout.getPlid()));
			}

			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(), ctCollection.getCtCollectionId());

			_updateLayoutClassedModelUsages(ctCollection.getCtCollectionId());

			_runUpgrade(
				() -> _assertLayoutClassedModelUsages(
					0, _journalArticle.getResourcePrimKey(),
					_draftLayout.getPlid(), _layout.getPlid()));
		}
	}

	private void _assertLayoutClassedModelUsages(
			long ctCollectionId, long classPK, long... plids)
		throws Exception {

		for (long plid : plids) {
			List<LayoutClassedModelUsage> layoutClassedModelUsages =
				_layoutClassedModelUsageLocalService.
					getLayoutClassedModelUsagesByPlid(plid);

			Assert.assertEquals(
				layoutClassedModelUsages.toString(), 2,
				layoutClassedModelUsages.size());

			for (LayoutClassedModelUsage layoutClassedModelUsage :
					layoutClassedModelUsages) {

				Assert.assertEquals(
					classPK, layoutClassedModelUsage.getClassPK());

				Assert.assertEquals(
					ctCollectionId,
					layoutClassedModelUsage.getCtCollectionId());

				if (_portal.getClassNameId(FragmentEntryLink.class) ==
						layoutClassedModelUsage.getContainerType()) {

					FragmentEntryLink fragmentEntryLink =
						_fragmentEntryLinkLocalService.getFragmentEntryLink(
							GetterUtil.getLong(
								layoutClassedModelUsage.getContainerKey()));

					Assert.assertEquals(plid, fragmentEntryLink.getPlid());
				}
				else {
					Assert.assertEquals(
						_portal.getClassNameId(
							LayoutPageTemplateStructure.class),
						layoutClassedModelUsage.getContainerType());

					LayoutPageTemplateStructure layoutPageTemplateStructure =
						_layoutPageTemplateStructureLocalService.
							getLayoutPageTemplateStructure(
								GetterUtil.getLong(
									layoutClassedModelUsage.getContainerKey()));

					Assert.assertEquals(
						plid, layoutPageTemplateStructure.getPlid());
				}
			}
		}
	}

	private void _runUpgrade(UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		for (DataCleanup dataCleanup :
				DataCleanupUtil.getSystemDataCleanups()) {

			if (StringUtil.equals(
					dataCleanup.getLabel(),
					"remove-layout-classed-model-usage-orphan-data")) {

				dataCleanup.cleanup();

				break;
			}
		}

		unsafeRunnable.run();
	}

	private void _setUp() throws Exception {
		_layout = LayoutTestUtil.addTypeContentPublishedLayout(
			_groupLocalService.fetchGroup(TestPropsValues.getGroupId()),
			RandomTestUtil.randomString(), WorkflowConstants.STATUS_APPROVED);

		_draftLayout = _layout.fetchDraftLayout();

		_journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getGroupId(), 0);

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_draftLayout.getPlid());

		ContentLayoutTestUtil.addItemToLayout(
			JSONUtil.put(
				"styles",
				JSONUtil.put(
					"backgroundImage",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						_portal.getClassNameId(JournalArticle.class)
					).put(
						"classPK", _journalArticle.getResourcePrimKey()
					).put(
						"fieldId", "JournalArticle_authorProfileImage"
					))
			).toString(),
			LayoutDataItemTypeConstants.TYPE_CONTAINER, _draftLayout,
			_layoutStructureProvider, segmentsExperienceId);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				RandomTestUtil.randomString(), StringPool.BLANK,
				serviceContext);

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.addFragmentEntry(
				null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				StringPool.BLANK,
				"<h1 data-lfr-editable-id=\"element-text\" " +
					"data-lfr-editable-type=\"text\">Heading Example</h1>",
				StringPool.BLANK, false, StringPool.BLANK, null, 0, false,
				false, FragmentConstants.TYPE_COMPONENT, null,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"element-text",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						_portal.getClassNameId(JournalArticle.class)
					).put(
						"classPK", _journalArticle.getResourcePrimKey()
					).put(
						"fieldId", "JournalArticle_title"
					))
			).toString(),
			fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
			fragmentEntry.getExternalReferenceCode(), null,
			fragmentEntry.getHtml(), fragmentEntry.getJs(), _draftLayout,
			fragmentEntry.getFragmentEntryKey(), segmentsExperienceId,
			fragmentEntry.getType());

		ContentLayoutTestUtil.publishLayout(_draftLayout, _layout);
	}

	private void _updateLayoutClassedModelUsages(long ctCollectionId) {
		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(_layout.getPlid());

		List<LayoutClassedModelUsage> draftLayoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(_draftLayout.getPlid());

		Map<Long, LayoutClassedModelUsage> layoutClassedModelUsageMap =
			new HashMap<>();

		for (LayoutClassedModelUsage draftLayoutClassedModelUsage :
				draftLayoutClassedModelUsages) {

			layoutClassedModelUsageMap.put(
				draftLayoutClassedModelUsage.getContainerType(),
				draftLayoutClassedModelUsage);
		}

		for (LayoutClassedModelUsage layoutClassedModelUsage :
				layoutClassedModelUsages) {

			LayoutClassedModelUsage draftLayoutClassedModelUsage =
				layoutClassedModelUsageMap.get(
					layoutClassedModelUsage.getContainerType());

			String layoutContainerKey =
				layoutClassedModelUsage.getContainerKey();

			layoutClassedModelUsage.setCtCollectionId(ctCollectionId);

			layoutClassedModelUsage.setContainerKey(
				draftLayoutClassedModelUsage.getContainerKey());

			draftLayoutClassedModelUsage.setCtCollectionId(ctCollectionId);

			draftLayoutClassedModelUsage.setContainerKey(layoutContainerKey);

			_layoutClassedModelUsageLocalService.updateLayoutClassedModelUsage(
				layoutClassedModelUsage);

			_layoutClassedModelUsageLocalService.updateLayoutClassedModelUsage(
				draftLayoutClassedModelUsage);
		}
	}

	private void _updateLayoutClassedModelUsages(
		long ctCollectionId, long... plids) {

		for (long plid : plids) {
			List<LayoutClassedModelUsage> layoutClassedModelUsages =
				_layoutClassedModelUsageLocalService.
					getLayoutClassedModelUsagesByPlid(plid);

			for (LayoutClassedModelUsage layoutClassedModelUsage :
					layoutClassedModelUsages) {

				layoutClassedModelUsage.setCtCollectionId(ctCollectionId);

				layoutClassedModelUsage.setContainerKey(
					String.valueOf(RandomTestUtil.randomLong()));

				_layoutClassedModelUsageLocalService.
					updateLayoutClassedModelUsage(layoutClassedModelUsage);
			}
		}
	}

	@Inject
	private CTCollectionService _ctCollectionService;

	private Layout _draftLayout;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private JournalArticle _journalArticle;
	private Layout _layout;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}