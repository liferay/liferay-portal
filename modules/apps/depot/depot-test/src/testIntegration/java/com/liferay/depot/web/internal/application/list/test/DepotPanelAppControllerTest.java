/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.application.list.test;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.memberships.constants.SiteMembershipsPortletKeys;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class DepotPanelAppControllerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_depotEntry = _addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY);
		_designLibraryDepotEntry = _addDepotEntry(
			DepotConstants.TYPE_DESIGN_LIBRARY);
	}

	@Test
	@TestInfo("LPD-110391")
	public void testGetPanelApps() throws Exception {
		_testGetPanelAppsHidesAllApplicationsInTheCategorizationCategoryForADesignLibraryGroup();
		_testGetPanelAppsHidesAllApplicationsInTheContentCategoryForADesignLibraryGroup();
		_testGetPanelAppsHidesContentSetsInTheSiteBuilderCategoryForADesignLibraryGroup();
		_testGetPanelAppsShowsAllContentWithSites();
		_testGetPanelAppsShowsExportAndImportAndStagingInTheStagingCategoryForADepotGroup();
		_testGetPanelAppsShowsOnlyContentSetsInTheSiteBuilderCategoryForADepotGroup();
		_testGetPanelAppsShowsOnlyDocumentsAndMediaAndWebContentInTheContentCategoryForADepotGroup();
		_testGetPanelAppsShowsOnlyMembersInThePeopleCategoryForADepotGroup();
		_testGetPanelAppsShowsTheCategorizationCategoryForADepotGroup();
		_testGetPanelAppsShowsTheControlPanelCategoryForADepotGroup();
		_testGetPanelAppsShowsTheRecycleBinCategoryForADepotGroup();
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			new HashMap<>(), type, ServiceContextTestUtil.getServiceContext());
	}

	private void _assertIsDisplayed(long groupId, String parentPanelCategoryKey)
		throws Exception {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			parentPanelCategoryKey,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(groupId));

		Assert.assertFalse(panelApps.isEmpty());
	}

	private void _assertPanelAppsContain(
		List<PanelApp> panelApps, String portletId) {

		boolean found = false;

		for (PanelApp panelApp : panelApps) {
			if (Objects.equals(portletId, panelApp.getPortletId())) {
				found = true;

				break;
			}
		}

		Assert.assertTrue(
			"Panel apps do not contain portlet " + portletId, found);
	}

	private void _testGetPanelAppsHidesAllApplicationsInTheCategorizationCategoryForADesignLibraryGroup()
		throws Exception {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_CATEGORIZATION,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(_designLibraryDepotEntry.getGroupId()));

		Assert.assertTrue(panelApps.isEmpty());
	}

	private void _testGetPanelAppsHidesAllApplicationsInTheContentCategoryForADesignLibraryGroup()
		throws Exception {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(_designLibraryDepotEntry.getGroupId()));

		Assert.assertTrue(panelApps.isEmpty());
	}

	private void _testGetPanelAppsHidesContentSetsInTheSiteBuilderCategoryForADesignLibraryGroup()
		throws Exception {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_BUILD,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(_designLibraryDepotEntry.getGroupId()));

		Assert.assertTrue(panelApps.isEmpty());
	}

	private void _testGetPanelAppsShowsAllContentWithSites() throws Exception {
		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(TestPropsValues.getGroupId()));

		Assert.assertTrue(panelApps.size() >= 2);
	}

	private void _testGetPanelAppsShowsExportAndImportAndStagingInTheStagingCategoryForADepotGroup()
		throws Exception {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_PUBLISHING,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(_depotEntry.getGroupId()));

		Assert.assertEquals(panelApps.toString(), 3, panelApps.size());

		_assertPanelAppsContain(panelApps, ExportImportPortletKeys.EXPORT);
		_assertPanelAppsContain(panelApps, ExportImportPortletKeys.IMPORT);
		_assertPanelAppsContain(
			panelApps, StagingProcessesPortletKeys.STAGING_PROCESSES);
	}

	private void _testGetPanelAppsShowsOnlyContentSetsInTheSiteBuilderCategoryForADepotGroup()
		throws Exception {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_BUILD,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(_depotEntry.getGroupId()));

		Assert.assertEquals(panelApps.toString(), 1, panelApps.size());

		_assertPanelAppsContain(panelApps, AssetListPortletKeys.ASSET_LIST);
	}

	private void _testGetPanelAppsShowsOnlyDocumentsAndMediaAndWebContentInTheContentCategoryForADepotGroup()
		throws Exception {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(_depotEntry.getGroupId()));

		Assert.assertEquals(panelApps.toString(), 3, panelApps.size());

		_assertPanelAppsContain(
			panelApps, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN);
		_assertPanelAppsContain(panelApps, JournalPortletKeys.JOURNAL);
		_assertPanelAppsContain(panelApps, PortletKeys.TRANSLATION);
	}

	private void _testGetPanelAppsShowsOnlyMembersInThePeopleCategoryForADepotGroup()
		throws Exception {

		List<PanelApp> panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_MEMBERS,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(_depotEntry.getGroupId()));

		Assert.assertEquals(panelApps.toString(), 1, panelApps.size());

		_assertPanelAppsContain(
			panelApps, SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN);

		panelApps = _panelAppRegistry.getPanelApps(
			PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
			PermissionThreadLocal.getPermissionChecker(),
			_groupLocalService.getGroup(TestPropsValues.getGroupId()));

		Assert.assertTrue(panelApps.size() > 1);
	}

	private void _testGetPanelAppsShowsTheCategorizationCategoryForADepotGroup()
		throws Exception {

		_assertIsDisplayed(
			_depotEntry.getGroupId(),
			PanelCategoryKeys.SITE_ADMINISTRATION_CATEGORIZATION);
	}

	private void _testGetPanelAppsShowsTheControlPanelCategoryForADepotGroup()
		throws Exception {

		_assertIsDisplayed(
			_depotEntry.getGroupId(), PanelCategoryKeys.CONTROL_PANEL_APPS);
		_assertIsDisplayed(
			_depotEntry.getGroupId(), PanelCategoryKeys.CONTROL_PANEL_INSTANCE);
		_assertIsDisplayed(
			_depotEntry.getGroupId(), PanelCategoryKeys.CONTROL_PANEL_SITES);
		_assertIsDisplayed(
			_depotEntry.getGroupId(), PanelCategoryKeys.CONTROL_PANEL_USERS);
		_assertIsDisplayed(
			_depotEntry.getGroupId(), PanelCategoryKeys.CONTROL_PANEL_WORKFLOW);
		_assertIsDisplayed(
			_depotEntry.getGroupId(),
			PanelCategoryKeys.SITE_ADMINISTRATION_CONFIGURATION);
	}

	private void _testGetPanelAppsShowsTheRecycleBinCategoryForADepotGroup()
		throws Exception {

		_assertIsDisplayed(
			_depotEntry.getGroupId(),
			PanelCategoryKeys.SITE_ADMINISTRATION_RECYCLE_BIN);
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private DepotEntry _designLibraryDepotEntry;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private PanelAppRegistry _panelAppRegistry;

}