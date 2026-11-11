/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_6.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class CMSPlanningAndTasksPagesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_company);
	}

	@Test
	public void testUpgrade() throws Exception {

		// Upgrading adds the planning and tasks pages

		Group group = _groupLocalService.getGroup(
			_company.getCompanyId(), GroupConstants.CMS);

		_deleteLayoutByFriendlyURL(group, "/tasks");
		_deleteLayoutByFriendlyURL(group, "/planning");

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		Layout planningLayout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/planning");

		Assert.assertNotNull(planningLayout);
		Assert.assertEquals(
			LayoutConstants.TYPE_NODE, planningLayout.getType());
		Assert.assertEquals(
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			planningLayout.getParentLayoutId());

		_assertResourcePermissions(planningLayout);
		_assertNameMap(planningLayout, "planning");

		Layout tasksLayout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/tasks");

		Assert.assertNotNull(tasksLayout);
		Assert.assertEquals(
			LayoutConstants.TYPE_CONTENT, tasksLayout.getType());
		Assert.assertEquals(
			planningLayout.getLayoutId(), tasksLayout.getParentLayoutId());

		_assertResourcePermissions(tasksLayout);
		_assertTasksFragmentEntryLink(tasksLayout);

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, tasksLayout.getStatus());

		_assertNavigationMenuItems(group, tasksLayout);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				group.getGroupId(), "cms-master");

		if (masterLayoutPageTemplateEntry != null) {
			Assert.assertEquals(
				masterLayoutPageTemplateEntry.getExternalReferenceCode(),
				tasksLayout.getMasterLayoutPageTemplateEntryERC());
		}

		// Upgrading a second time does not create duplicate layouts

		upgradeProcess.upgrade();

		Assert.assertEquals(
			planningLayout.getPlid(),
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/planning"
			).getPlid());
		Assert.assertEquals(
			tasksLayout.getPlid(),
			_layoutLocalService.fetchLayoutByFriendlyURL(
				group.getGroupId(), false, "/tasks"
			).getPlid());
	}

	private void _assertNameMap(Layout layout, String key) {
		Map<Locale, String> nameMap = layout.getNameMap();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			Assert.assertEquals(
				LanguageUtil.get(locale, key), nameMap.get(locale));
		}
	}

	private void _assertNavigationMenuItems(Group group, Layout tasksLayout) {
		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					"CMSPRIMARYNAVIGATION", group.getGroupId());

		Assert.assertNotNull(siteNavigationMenu);

		SiteNavigationMenuItem planningSiteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					"CMSPRIMARYNAVIGATION11", group.getGroupId());

		Assert.assertNotNull(planningSiteNavigationMenuItem);
		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.NODE,
			planningSiteNavigationMenuItem.getType());
		Assert.assertEquals(
			0,
			planningSiteNavigationMenuItem.getParentSiteNavigationMenuItemId());

		SiteNavigationMenuItem tasksSiteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.
				fetchSiteNavigationMenuItemByExternalReferenceCode(
					"CMSPRIMARYNAVIGATION12", group.getGroupId());

		Assert.assertNotNull(tasksSiteNavigationMenuItem);
		Assert.assertEquals(
			SiteNavigationMenuItemTypeConstants.LAYOUT,
			tasksSiteNavigationMenuItem.getType());
		Assert.assertEquals(
			planningSiteNavigationMenuItem.getSiteNavigationMenuItemId(),
			tasksSiteNavigationMenuItem.getParentSiteNavigationMenuItemId());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.load(
			tasksSiteNavigationMenuItem.getTypeSettings()
		).build();

		Assert.assertEquals(
			tasksLayout.getExternalReferenceCode(),
			unicodeProperties.getProperty("externalReferenceCode"));
	}

	private void _assertResourcePermissions(Layout layout) throws Exception {
		long companyId = layout.getCompanyId();
		String primKey = String.valueOf(layout.getPlid());

		Role cmsAdministratorRole = _roleLocalService.getRole(
			companyId, RoleConstants.CMS_ADMINISTRATOR);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				companyId, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, primKey,
				cmsAdministratorRole.getRoleId(), "VIEW"));

		Role siteMemberRole = _roleLocalService.getRole(
			companyId, RoleConstants.SITE_MEMBER);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				companyId, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, primKey,
				siteMemberRole.getRoleId(), "VIEW"));

		Role guestRole = _roleLocalService.getRole(
			companyId, RoleConstants.GUEST);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				companyId, Layout.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL, primKey,
				guestRole.getRoleId(), "VIEW"));
	}

	private void _assertTasksFragmentEntryLink(Layout layout) throws Exception {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		LayoutStructureItem mainLayoutStructureItem =
			layoutStructure.getMainLayoutStructureItem();

		List<String> childrenItemIds =
			mainLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			(FragmentStyledLayoutStructureItem)
				layoutStructure.getLayoutStructureItem(childrenItemIds.get(0));

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentStyledLayoutStructureItem.getFragmentEntryLinkId());

		Assert.assertEquals(
			"com.liferay.site.cms.site.initializer.internal.fragment." +
				"renderer.ViewTasksJSPSectionFragmentRenderer",
			fragmentEntryLink.getRendererKey());
		Assert.assertEquals(
			FragmentConstants.TYPE_COMPONENT, fragmentEntryLink.getType());
	}

	private void _deleteLayoutByFriendlyURL(Group group, String friendlyURL)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, friendlyURL);

		if (layout != null) {
			_layoutLocalService.deleteLayout(layout);
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_6." +
			"CMSPlanningAndTasksPagesUpgradeProcess";

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}