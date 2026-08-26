/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentStagingTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkStagingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_liveGroup = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_liveGroup);
	}

	@Test
	public void testFragmentEntryLinkCopiedWhenLocalStagingActivated()
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		FragmentEntryLink liveFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					fragmentEntry.getGroupId(), _layout.getGroupId()),
				fragmentEntry.getHtml(), fragmentEntry.getJs(), _layout,
				fragmentEntry.getFragmentEntryKey(), fragmentEntry.getType(),
				null, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()));

		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		_fragmentEntryLinkLocalService.getFragmentEntryLinkByUuidAndGroupId(
			liveFragmentEntryLink.getUuid(), _stagingGroup.getGroupId());
	}

	@Test
	public void testPublishFragmentEntryDeletionWithPreviousFragmentEntryName()
		throws PortalException {

		FragmentCollection liveFragmentCollection =
			FragmentTestUtil.addFragmentCollection(_liveGroup.getGroupId());

		FragmentEntry liveFragmentEntry =
			FragmentEntryTestUtil.addFragmentEntry(
				liveFragmentCollection.getFragmentCollectionId());

		FragmentEntryLink liveFragmentEntryLink =
			FragmentTestUtil.addFragmentEntryLink(
				_liveGroup.getGroupId(), liveFragmentEntry.getFragmentEntryId(),
				_layout.getPlid());

		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		_fragmentEntryLinkLocalService.deleteFragmentEntryLinks(
			_stagingGroup.getGroupId());

		FragmentEntry stagingFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByUuidAndGroupId(
				liveFragmentEntry.getUuid(), _stagingGroup.getGroupId());

		_fragmentEntryLocalService.deleteFragmentEntry(stagingFragmentEntry);

		FragmentCollection stagingFragmentCollection =
			_fragmentCollectionLocalService.
				getFragmentCollectionByUuidAndGroupId(
					liveFragmentCollection.getUuid(),
					_stagingGroup.getGroupId());

		FragmentEntry newStagingFragmentEntry =
			FragmentEntryTestUtil.addFragmentEntry(
				stagingFragmentCollection.getFragmentCollectionId(),
				liveFragmentEntry.getName());

		Layout stagingLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_layout.getUuid(), _stagingGroup.getGroupId(), false);

		FragmentTestUtil.addFragmentEntryLink(
			_stagingGroup.getGroupId(),
			newStagingFragmentEntry.getFragmentEntryId(),
			stagingLayout.getPlid());

		FragmentStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		liveFragmentEntryLink =
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				liveFragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertNull(liveFragmentEntryLink);

		liveFragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
			liveFragmentEntry.getFragmentEntryId());

		Assert.assertNull(liveFragmentEntry);
	}

	@Test
	public void testPublishFragmentEntryLink() throws Exception {
		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		Layout stagingLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_layout.getUuid(), _stagingGroup.getGroupId(), false);

		Layout draftStagingLayout = stagingLayout.fetchDraftLayout();

		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-heading");

		FragmentEntryLink stagingFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				null, fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					fragmentEntry.getGroupId(),
					draftStagingLayout.getGroupId()),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				draftStagingLayout, fragmentEntry.getFragmentEntryKey(),
				fragmentEntry.getType(), null, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						draftStagingLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftStagingLayout, stagingLayout);

		FragmentStagingTestUtil.publishLayouts(_stagingGroup, _liveGroup);

		_fragmentEntryLinkLocalService.getFragmentEntryLinkByUuidAndGroupId(
			stagingFragmentEntryLink.getUuid(), _liveGroup.getGroupId());
	}

	@Test
	@TestInfo("LPS-128305")
	public void testPublishFragmentEntryLinkWithDLReference() throws Exception {
		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		FileEntry stagingFileEntry = DLAppTestUtil.addFileEntry(
			_stagingGroup.getGroupId());

		String href = StringBundler.concat(
			"/documents/", stagingFileEntry.getGroupId(), StringPool.SLASH,
			stagingFileEntry.getFolderId(), StringPool.SLASH,
			stagingFileEntry.getTitle(), StringPool.SLASH,
			stagingFileEntry.getUuid());

		Layout stagingLayout = _layoutLocalService.getLayoutByUuidAndGroupId(
			_layout.getUuid(), _stagingGroup.getGroupId(), false);

		Layout draftStagingLayout = stagingLayout.fetchDraftLayout();

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				"BASIC_COMPONENT-paragraph");

		String anchorHTML = StringBundler.concat(
			"<a href=\"", href, "\">DL Image</a>");

		FragmentEntryLink stagingFragmentEntryLink =
			ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"element-text",
						JSONUtil.put(
							"defaultValue", anchorHTML
						).put(
							"en_US", anchorHTML
						))
				).toString(),
				fragmentEntry.getCss(), fragmentEntry.getConfiguration(),
				fragmentEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					fragmentEntry.getGroupId(),
					draftStagingLayout.getGroupId()),
				fragmentEntry.getHtml(), fragmentEntry.getJs(),
				draftStagingLayout, fragmentEntry.getFragmentEntryKey(),
				fragmentEntry.getType(), null, 0,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(
						draftStagingLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftStagingLayout, stagingLayout);

		Map<String, String[]> parameterMap =
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap();

		long backgroundTaskId = StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			_liveGroup.getGroupId(), false, parameterMap);

		ExportImportTestUtil.assertBackgroundTaskSuccessful(backgroundTaskId);

		FragmentEntryLink liveFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLinkByUuidAndGroupId(
				stagingFragmentEntryLink.getUuid(), _liveGroup.getGroupId());

		String liveHTML = liveFragmentEntryLink.getEditableValuesJSONObject(
		).getJSONObject(
			FragmentEntryProcessorConstants.
				KEY_EDITABLE_FRAGMENT_ENTRY_PROCESSOR
		).getJSONObject(
			"element-text"
		).getString(
			"en_US"
		);

		Document document = SAXReaderUtil.read(liveHTML);

		Element rootElement = document.getRootElement();

		Assert.assertEquals("DL Image", rootElement.getText());

		String liveHref = rootElement.attributeValue("href");

		FileEntry liveFileEntry =
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				stagingFileEntry.getUuid(), _liveGroup.getGroupId());

		Assert.assertTrue(liveHref.contains(liveFileEntry.getUuid()));
		Assert.assertNotEquals(
			stagingFileEntry.getFileEntryId(), liveFileEntry.getFileEntryId());
	}

	@Test
	public void testValidateFragmentEntryAfterDeactivateStaging()
		throws PortalException {

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_liveGroup.getGroupId());

		FragmentEntry fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());

		FragmentEntryLink liveFragmentEntryLink =
			FragmentTestUtil.addFragmentEntryLink(
				_liveGroup.getGroupId(), fragmentEntry.getFragmentEntryId(),
				_layout.getPlid());

		_stagingGroup = FragmentStagingTestUtil.enableLocalStaging(_liveGroup);

		FragmentStagingTestUtil.publishLayoutsRangeFromLastPublishedDate(
			_stagingGroup, _liveGroup);

		liveFragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLinkByUuidAndGroupId(
				liveFragmentEntryLink.getUuid(), _liveGroup.getGroupId());

		Long groupId1 = ScopeUtil.getItemGroupId(
			liveFragmentEntryLink.getCompanyId(),
			liveFragmentEntryLink.getFragmentEntryScopeERC(),
			liveFragmentEntryLink.getGroupId());

		FragmentEntry liveFragmentEntry =
			_fragmentEntryLocalService.getFragmentEntryByExternalReferenceCode(
				liveFragmentEntryLink.getFragmentEntryERC(), groupId1);

		Assert.assertEquals(
			liveFragmentEntryLink.getGroupId(), liveFragmentEntry.getGroupId());

		_stagingLocalService.disableStaging(_liveGroup, new ServiceContext());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLinkByUuidAndGroupId(
				liveFragmentEntryLink.getUuid(), _liveGroup.getGroupId());

		Assert.assertNotNull(
			_fragmentEntryLocalService.getFragmentEntryByExternalReferenceCode(
				fragmentEntryLink.getFragmentEntryERC(),
				ScopeUtil.getItemGroupId(
					fragmentEntryLink.getCompanyId(),
					fragmentEntryLink.getFragmentEntryScopeERC(),
					fragmentEntryLink.getGroupId())));
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	private Group _stagingGroup;

	@Inject
	private StagingLocalService _stagingLocalService;

}