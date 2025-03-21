/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v5_2_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.exportimport.changeset.Changeset;
import com.liferay.exportimport.changeset.portlet.action.ExportImportChangesetMVCActionCommandHelper;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.service.LayoutClassedModelUsageLocalService;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutRevisionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class JournalArticleLayoutClassedModelUsageUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
		_originalServiceContext = ServiceContextThreadLocal.getServiceContext();

		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_user = UserTestUtil.getAdminUser(_company.getCompanyId());

		PrincipalThreadLocal.setName(_user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		_liveGroup = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(
			_liveGroup.getGroupId(), false);

		_portletId = LayoutTestUtil.addPortletToLayout(
			_layout, AssetPublisherPortletKeys.ASSET_PUBLISHER,
			Collections.emptyMap());

		_pushServiceContext(_liveGroup, _layout);

		_stagingLocalService.enableLocalStaging(
			_user.getUserId(), _liveGroup, true, true,
			ServiceContextThreadLocal.getServiceContext());

		_liveGroup = _groupLocalService.getGroup(_liveGroup.getGroupId());

		_journalArticle = JournalTestUtil.addArticle(_company.getGroupId(), 0);

		JournalArticleResource journalArticleResource =
			_journalArticleResourceLocalService.getArticleResource(
				_journalArticle.getResourcePrimKey());

		_assetEntry = _assetEntryLocalService.getEntry(
			_company.getGroupId(), journalArticleResource.getUuid());
	}

	@After
	public void tearDown() throws Exception {
		try {
			_pushServiceContext(_liveGroup, _layout);

			_stagingLocalService.disableStaging(
				_liveGroup, ServiceContextThreadLocal.getServiceContext());
		}
		catch (Exception exception) {
		}

		PrincipalThreadLocal.setName(_originalName);
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		ServiceContextThreadLocal.pushServiceContext(_originalServiceContext);
	}

	@Test
	public void testUpgrade() throws Exception {
		_assertAssetEntryLayoutClassedModelUsages();

		Group stagingGroup = _liveGroup.getStagingGroup();

		Layout stagingLayout = _getStagingLayout(stagingGroup.getGroupId());

		_pushServiceContext(stagingGroup, stagingLayout);

		_updateAssetPublisherPortletPreference(stagingLayout);

		_assertAssetEntryLayoutClassedModelUsages(stagingLayout);

		_publishToLive(stagingLayout);

		_assertAssetEntryLayoutClassedModelUsages(stagingLayout, _layout);

		LayoutRevision layoutRevision = _getLayoutRevision(stagingLayout);

		_updateLayoutClassedModelUsagePlid(stagingLayout, layoutRevision);

		_runUpgrade();

		_assertLayoutClassedModelUsagesByPlidCount(1, stagingLayout.getPlid());

		_assertLayoutClassedModelUsagesByPlidCount(
			0, layoutRevision.getLayoutRevisionId());
	}

	@Test
	public void testUpgradeExistingLayoutClassedModelUsagesByPlid()
		throws Exception {

		_assertAssetEntryLayoutClassedModelUsages();

		Group stagingGroup = _liveGroup.getStagingGroup();

		Layout stagingLayout = _getStagingLayout(stagingGroup.getGroupId());

		_pushServiceContext(stagingGroup, stagingLayout);

		_updateAssetPublisherPortletPreference(stagingLayout);

		_assertAssetEntryLayoutClassedModelUsages(stagingLayout);

		_publishToLive(stagingLayout);

		_assertAssetEntryLayoutClassedModelUsages(stagingLayout, _layout);

		LayoutRevision layoutRevision = _getLayoutRevision(stagingLayout);

		_addLayoutClassedModelUsage(stagingLayout, layoutRevision);

		_runUpgrade();

		_assertLayoutClassedModelUsagesByPlidCount(1, stagingLayout.getPlid());

		_assertLayoutClassedModelUsagesByPlidCount(
			0, layoutRevision.getLayoutRevisionId());
	}

	private void _addLayoutClassedModelUsage(
		Layout layout, LayoutRevision layoutRevision) {

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(layout.getPlid());

		Assert.assertEquals(
			layoutClassedModelUsages.toString(), 1,
			layoutClassedModelUsages.size());

		LayoutClassedModelUsage layoutClassedModelUsage =
			layoutClassedModelUsages.get(0);

		_layoutClassedModelUsageLocalService.addLayoutClassedModelUsage(
			layoutClassedModelUsage.getGroupId(),
			layoutClassedModelUsage.getClassNameId(),
			layoutClassedModelUsage.getClassPK(),
			layoutClassedModelUsage.getClassedModelExternalReferenceCode(),
			layoutClassedModelUsage.getContainerKey(),
			layoutClassedModelUsage.getContainerType(),
			layoutRevision.getLayoutRevisionId(),
			ServiceContextThreadLocal.getServiceContext());

		_assertLayoutClassedModelUsagesByPlidCount(1, layout.getPlid());

		_assertLayoutClassedModelUsagesByPlidCount(
			1, layoutRevision.getLayoutRevisionId());
	}

	private void _assertAssetEntryLayoutClassedModelUsages(Layout... layouts) {
		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.getLayoutClassedModelUsages(
				_assetEntry.getClassNameId(), _assetEntry.getClassPK());

		Assert.assertEquals(
			layoutClassedModelUsages.toString(), layouts.length,
			layoutClassedModelUsages.size());

		for (Layout layout : layouts) {
			LayoutClassedModelUsage layoutClassedModelUsage = null;

			for (LayoutClassedModelUsage layoutClassedModelUsage1 :
					layoutClassedModelUsages) {

				if (layoutClassedModelUsage1.getPlid() == layout.getPlid()) {
					layoutClassedModelUsage = layoutClassedModelUsage1;

					break;
				}
			}

			Assert.assertNotNull(layoutClassedModelUsage);

			Assert.assertEquals(
				_assetEntry.getClassNameId(),
				layoutClassedModelUsage.getClassNameId());

			Assert.assertEquals(
				_assetEntry.getClassPK(), layoutClassedModelUsage.getClassPK());

			Assert.assertEquals(
				layout.getPlid(), layoutClassedModelUsage.getPlid());

			Assert.assertEquals(
				layout.getGroupId(), layoutClassedModelUsage.getGroupId());

			Assert.assertEquals(
				_portletId, layoutClassedModelUsage.getContainerKey());
		}
	}

	private void _assertLayoutClassedModelUsagesByPlidCount(
		int expected, long plid) {

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(plid);

		Assert.assertEquals(
			layoutClassedModelUsages.toString(), expected,
			layoutClassedModelUsages.size());
	}

	private String _getAssetEntryXml() throws Exception {
		Document document = SAXReaderUtil.createDocument(StringPool.UTF8);

		Element assetEntryElement = document.addElement("asset-entry");

		assetEntryElement.addElement("asset-entry-type");

		Element assetEntryUuidElement = assetEntryElement.addElement(
			"asset-entry-uuid");

		assetEntryUuidElement.addText(_assetEntry.getClassUuid());

		return document.formattedString(StringPool.BLANK);
	}

	private LayoutRevision _getLayoutRevision(Layout stagingLayout) {
		List<LayoutRevision> layoutRevisions =
			_layoutRevisionLocalService.getLayoutRevisions(
				stagingLayout.getPlid());

		Assert.assertEquals(
			layoutRevisions.toString(), 1, layoutRevisions.size());

		return layoutRevisions.get(0);
	}

	private Layout _getStagingLayout(long stagingGroupId) {
		List<Layout> stagingLayouts = _layoutLocalService.getLayouts(
			stagingGroupId, false);

		Assert.assertEquals(
			stagingLayouts.toString(), 1, stagingLayouts.size());

		Layout stagingLayout = stagingLayouts.get(0);

		Assert.assertEquals(_layout.getUuid(), stagingLayout.getUuid());

		return stagingLayout;
	}

	private void _publishToLive(Layout layout) throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.PORTLET_ID, LayoutAdminPortletKeys.GROUP_PAGES);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, serviceContext.getThemeDisplay());

		mockLiferayPortletActionRequest.setParameter(
			"groupId", String.valueOf(layout.getGroupId()));

		Changeset.Builder builder = Changeset.create();

		Changeset changeset = builder.addStagedModel(
			() -> layout
		).addMultipleStagedModel(
			Collections::emptyList
		).build();

		_exportImportChangesetMVCActionCommandHelper.publish(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse(), changeset);
	}

	private void _pushServiceContext(Group group, Layout layout)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), _user.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		ThemeDisplay themeDisplay = ContentLayoutTestUtil.getThemeDisplay(
			_company, group, layout);

		themeDisplay.setRequest(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		serviceContext.setRequest(mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			_multiVMPool.clear();
		}
	}

	private void _updateAssetPublisherPortletPreference(Layout layout)
		throws Exception {

		PortletPreferences portletPreferences =
			_portletPreferencesFactory.getPortletSetup(
				layout, _portletId, null);

		portletPreferences.setValues("assetEntryXml", _getAssetEntryXml());
		portletPreferences.setValue(
			"scopeIds", "Group_" + _assetEntry.getGroupId());
		portletPreferences.setValues("selectionStyle", "manual");

		portletPreferences.store();

		_portletLayoutListener.onSetup(_portletId, layout.getPlid());
	}

	private void _updateLayoutClassedModelUsagePlid(
		Layout layout, LayoutRevision layoutRevision) {

		List<LayoutClassedModelUsage> layoutClassedModelUsages =
			_layoutClassedModelUsageLocalService.
				getLayoutClassedModelUsagesByPlid(layout.getPlid());

		Assert.assertEquals(
			layoutClassedModelUsages.toString(), 1,
			layoutClassedModelUsages.size());

		LayoutClassedModelUsage layoutClassedModelUsage =
			layoutClassedModelUsages.get(0);

		layoutClassedModelUsage.setPlid(layoutRevision.getLayoutRevisionId());

		_layoutClassedModelUsageLocalService.updateLayoutClassedModelUsage(
			layoutClassedModelUsage);

		_assertLayoutClassedModelUsagesByPlidCount(0, layout.getPlid());

		_assertLayoutClassedModelUsagesByPlidCount(
			1, layoutRevision.getLayoutRevisionId());
	}

	private static final String _CLASS_NAME =
		"com.liferay.journal.internal.upgrade.v5_2_1." +
			"JournalArticleLayoutClassedModelUsageUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.journal.internal.upgrade.registry.JournalServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	private AssetEntry _assetEntry;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ExportImportChangesetMVCActionCommandHelper
		_exportImportChangesetMVCActionCommandHelper;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

	private Layout _layout;

	@Inject
	private LayoutClassedModelUsageLocalService
		_layoutClassedModelUsageLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutRevisionLocalService _layoutRevisionLocalService;

	@DeleteAfterTestRun
	private Group _liveGroup;

	@Inject
	private MultiVMPool _multiVMPool;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;
	private ServiceContext _originalServiceContext;
	private String _portletId;

	@Inject(
		filter = "(&(component.name=com.liferay.asset.publisher.web.internal.portlet.layout.listener.AssetPublisherPortletLayoutListener))"
	)
	private PortletLayoutListener _portletLayoutListener;

	@Inject
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Inject
	private StagingLocalService _stagingLocalService;

	private User _user;

}