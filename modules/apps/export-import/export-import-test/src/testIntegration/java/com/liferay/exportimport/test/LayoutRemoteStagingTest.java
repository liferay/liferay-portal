/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.io.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class LayoutRemoteStagingTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-110538")
	public void testPublishLayoutsWithRemoteStagingMappedContent()
		throws Exception {

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET",
					"F0E1D2C3B4A5968778695A4B3C2D1E0F");
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET_HEX", true)) {

			_testPublishLayoutsWithRemoteStagingMappedContentInDepotGroup();
			_testPublishLayoutsWithRemoteStagingMappedContentInSiteGroup();
		}
	}

	@Test
	@TestInfo("LPD-110538")
	public void testPublishPortletWithRemoteStagingGroupExternalReferenceCodeMappings()
		throws Exception {

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET",
					"F0E1D2C3B4A5968778695A4B3C2D1E0F");
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_SHARED_SECRET_HEX", true)) {

			_testPublishPortletWithRemoteStagingGroupExternalReferenceCodeMappingsInLayoutScopeGroup();
			_testPublishPortletWithRemoteStagingGroupExternalReferenceCodeMappingsInSiteGroup();
		}
	}

	private void _disableRemoteStaging(Group group) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		serviceContext.setAttribute("forceDisable", Boolean.TRUE);

		_stagingLocalService.disableStaging(null, group, serviceContext);
	}

	private void _enableRemoteStaging(Group liveGroup, Group stagingGroup)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(stagingGroup.getGroupId());

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		UserTestUtil.setUser(TestPropsValues.getUser());

		_stagingLocalService.enableRemoteStaging(
			TestPropsValues.getUserId(), stagingGroup, false, false,
			"localhost", _portal.getPortalServerPort(false),
			_portal.getPathContext(), false, liveGroup.getGroupId(),
			serviceContext);

		liveGroup = _groupLocalService.getGroup(liveGroup.getGroupId());

		UnicodeProperties typeSettingsUnicodeProperties =
			liveGroup.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"stagedRemotely", Boolean.TRUE.toString());

		_groupLocalService.updateGroup(
			liveGroup.getGroupId(), typeSettingsUnicodeProperties.toString());
	}

	private void _publishRemotely(Group liveGroup, Group stagingGroup)
		throws Exception {

		StagingUtil.publishLayouts(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			liveGroup.getGroupId(), false,
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap());
	}

	private void _testPublishLayoutsWithRemoteStagingMappedContent(
			Group remoteLiveGroup, Group remoteLiveScopeGroup,
			Group remoteStagingGroup, Group remoteStagingScopeGroup)
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			remoteStagingScopeGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_publishRemotely(remoteLiveScopeGroup, remoteStagingScopeGroup);

		Layout layout = LayoutTestUtil.addTypeContentLayout(remoteStagingGroup);

		Layout draftLayout = layout.fetchDraftLayout();

		ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			JSONUtil.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"itemSelector",
					JSONUtil.put(
						"className", JournalArticle.class.getName()
					).put(
						"classNameId",
						_portal.getClassNameId(JournalArticle.class)
					).put(
						"classPK", journalArticle.getResourcePrimKey()
					).put(
						"classTypeId", journalArticle.getDDMStructureId()
					).put(
						"externalReferenceCode",
						journalArticle.getExternalReferenceCode()
					).put(
						"scopeExternalReferenceCode",
						remoteStagingScopeGroup.getExternalReferenceCode()
					))
			).toString(),
			_fragmentRendererRegistry.getFragmentRenderer(
				"com.liferay.fragment.internal.renderer." +
					"ContentObjectFragmentRenderer"),
			draftLayout, null, 0,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid()));

		ContentLayoutTestUtil.publishLayout(draftLayout, layout);

		_publishRemotely(remoteLiveGroup, remoteStagingGroup);

		Layout remoteLiveLayout =
			_layoutLocalService.getLayoutByExternalReferenceCode(
				layout.getExternalReferenceCode(),
				remoteLiveGroup.getGroupId());

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				remoteLiveGroup.getGroupId(), remoteLiveLayout.getPlid());

		Assert.assertEquals(
			fragmentEntryLinks.toString(), 1, fragmentEntryLinks.size());

		FragmentEntryLink fragmentEntryLink = fragmentEntryLinks.get(0);

		JSONObject itemSelectorJSONObject = JSONUtil.getValueAsJSONObject(
			JSONFactoryUtil.createJSONObject(
				fragmentEntryLink.getEditableValues()),
			"JSONObject/" +
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
			"JSONObject/itemSelector");

		Assert.assertEquals(
			remoteLiveScopeGroup.getExternalReferenceCode(),
			itemSelectorJSONObject.getString("scopeExternalReferenceCode"));
	}

	private void _testPublishLayoutsWithRemoteStagingMappedContentInDepotGroup()
		throws Exception {

		DepotEntry remoteLiveDepotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());
		Group remoteLiveGroup = GroupTestUtil.addGroup();
		DepotEntry remoteStagingDepotEntry =
			_depotEntryLocalService.addDepotEntry(
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				DepotConstants.TYPE_ASSET_LIBRARY,
				ServiceContextTestUtil.getServiceContext());
		Group remoteStagingGroup = GroupTestUtil.addGroup();

		try {
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				remoteLiveDepotEntry.getDepotEntryId(),
				remoteLiveGroup.getGroupId());
			_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
				remoteStagingDepotEntry.getDepotEntryId(),
				remoteStagingGroup.getGroupId());

			Group remoteLiveDepotGroup = remoteLiveDepotEntry.getGroup();
			Group remoteStagingDepotGroup = remoteStagingDepotEntry.getGroup();

			_enableRemoteStaging(remoteLiveDepotGroup, remoteStagingDepotGroup);

			_enableRemoteStaging(remoteLiveGroup, remoteStagingGroup);

			_testPublishLayoutsWithRemoteStagingMappedContent(
				remoteLiveGroup, remoteLiveDepotGroup, remoteStagingGroup,
				remoteStagingDepotGroup);
		}
		finally {
			_disableRemoteStaging(remoteStagingDepotEntry.getGroup());
			_disableRemoteStaging(remoteStagingGroup);

			_groupLocalService.deleteGroup(remoteStagingGroup);
			_groupLocalService.deleteGroup(remoteLiveGroup);

			_depotEntryLocalService.deleteDepotEntry(remoteStagingDepotEntry);
			_depotEntryLocalService.deleteDepotEntry(remoteLiveDepotEntry);
		}
	}

	private void _testPublishLayoutsWithRemoteStagingMappedContentInSiteGroup()
		throws Exception {

		Group remoteLiveGroup = GroupTestUtil.addGroup();
		Group remoteLiveScopeGroup = GroupTestUtil.addGroup();
		Group remoteStagingGroup = GroupTestUtil.addGroup();
		Group remoteStagingScopeGroup = GroupTestUtil.addGroup();

		try {
			_enableRemoteStaging(remoteLiveGroup, remoteStagingGroup);
			_enableRemoteStaging(remoteLiveScopeGroup, remoteStagingScopeGroup);

			_testPublishLayoutsWithRemoteStagingMappedContent(
				remoteLiveGroup, remoteLiveScopeGroup, remoteStagingGroup,
				remoteStagingScopeGroup);
		}
		finally {
			_disableRemoteStaging(remoteStagingGroup);
			_disableRemoteStaging(remoteStagingScopeGroup);

			_groupLocalService.deleteGroup(remoteStagingScopeGroup);
			_groupLocalService.deleteGroup(remoteStagingGroup);
			_groupLocalService.deleteGroup(remoteLiveScopeGroup);
			_groupLocalService.deleteGroup(remoteLiveGroup);
		}
	}

	private void
			_testPublishPortletWithRemoteStagingGroupExternalReferenceCodeMappings(
				Group remoteLiveScopeGroup, Group remoteStagingScopeGroup,
				Group sourceGroup)
		throws Exception {

		Layout controlPanelLayout = _layoutLocalService.getLayout(
			_portal.getControlPanelPlid(sourceGroup.getCompanyId()));

		ExportImportThreadLocal.setPortletStagingInProcess(true);

		File file = null;

		try {
			file = ExportImportTestUtil.exportPortletInfoAsFile(
				sourceGroup, controlPanelLayout, JournalPortletKeys.JOURNAL);
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}

		try (ZipReader zipReader = _zipReaderFactory.getZipReader(file)) {
			Document document = SAXReaderUtil.read(
				zipReader.getEntryAsString("/manifest.xml"));

			Element rootElement = document.getRootElement();

			Element headerElement = rootElement.element("header");

			Element groupExternalReferenceCodeMappingsElement =
				headerElement.element("group-external-reference-code-mappings");

			Map<String, String> liveExternalReferenceCodes = new HashMap<>();

			for (Element groupExternalReferenceCodeMappingElement :
					groupExternalReferenceCodeMappingsElement.elements(
						"group-external-reference-code-mapping")) {

				liveExternalReferenceCodes.put(
					groupExternalReferenceCodeMappingElement.attributeValue(
						"external-reference-code"),
					groupExternalReferenceCodeMappingElement.attributeValue(
						"live-external-reference-code"));
			}

			Assert.assertEquals(
				remoteLiveScopeGroup.getExternalReferenceCode(),
				liveExternalReferenceCodes.get(
					remoteStagingScopeGroup.getExternalReferenceCode()));
		}
		finally {
			FileUtil.delete(file);
		}
	}

	private void _testPublishPortletWithRemoteStagingGroupExternalReferenceCodeMappingsInLayoutScopeGroup()
		throws Exception {

		Group remoteLiveGroup = GroupTestUtil.addGroup();
		Group remoteLiveScopeGroup = GroupTestUtil.addGroup();
		Group remoteStagingGroup = GroupTestUtil.addGroup();
		Group remoteStagingScopeGroup = GroupTestUtil.addGroup();

		try {
			_enableRemoteStaging(remoteLiveGroup, remoteStagingGroup);
			_enableRemoteStaging(remoteLiveScopeGroup, remoteStagingScopeGroup);

			_testPublishPortletWithRemoteStagingGroupExternalReferenceCodeMappings(
				remoteLiveScopeGroup, remoteStagingScopeGroup,
				GroupTestUtil.addGroup(
					TestPropsValues.getUserId(),
					LayoutTestUtil.addTypeContentLayout(remoteStagingGroup)));
		}
		finally {
			_disableRemoteStaging(remoteStagingGroup);
			_disableRemoteStaging(remoteStagingScopeGroup);

			_groupLocalService.deleteGroup(remoteStagingScopeGroup);
			_groupLocalService.deleteGroup(remoteStagingGroup);
			_groupLocalService.deleteGroup(remoteLiveScopeGroup);
			_groupLocalService.deleteGroup(remoteLiveGroup);
		}
	}

	private void _testPublishPortletWithRemoteStagingGroupExternalReferenceCodeMappingsInSiteGroup()
		throws Exception {

		Group remoteLiveGroup = GroupTestUtil.addGroup();
		Group remoteLiveScopeGroup = GroupTestUtil.addGroup();
		Group remoteStagingGroup = GroupTestUtil.addGroup();
		Group remoteStagingScopeGroup = GroupTestUtil.addGroup();

		try {
			_enableRemoteStaging(remoteLiveGroup, remoteStagingGroup);
			_enableRemoteStaging(remoteLiveScopeGroup, remoteStagingScopeGroup);

			_testPublishPortletWithRemoteStagingGroupExternalReferenceCodeMappings(
				remoteLiveScopeGroup, remoteStagingScopeGroup,
				remoteStagingGroup);
		}
		finally {
			_disableRemoteStaging(remoteStagingGroup);
			_disableRemoteStaging(remoteStagingScopeGroup);

			_groupLocalService.deleteGroup(remoteStagingScopeGroup);
			_groupLocalService.deleteGroup(remoteStagingGroup);
			_groupLocalService.deleteGroup(remoteLiveScopeGroup);
			_groupLocalService.deleteGroup(remoteLiveGroup);
		}
	}

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private StagingLocalService _stagingLocalService;

	@Inject
	private ZipReaderFactory _zipReaderFactory;

}