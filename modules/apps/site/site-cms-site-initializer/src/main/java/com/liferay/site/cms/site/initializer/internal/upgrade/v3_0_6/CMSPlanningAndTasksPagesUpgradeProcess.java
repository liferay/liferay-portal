/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_6;

import com.liferay.layout.importer.LayoutsImporter;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.util.UpgradeProcessUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.cms.site.initializer.internal.fragment.renderer.ViewTasksJSPSectionFragmentRenderer;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Jan Brychta
 */
public class CMSPlanningAndTasksPagesUpgradeProcess extends UpgradeProcess {

	public CMSPlanningAndTasksPagesUpgradeProcess(
		CompanyLocalService companyLocalService,
		GroupLocalService groupLocalService,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService,
		LayoutsImporter layoutsImporter,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService,
		SegmentsExperienceLocalService segmentsExperienceLocalService,
		SiteNavigationMenuItemLocalService siteNavigationMenuItemLocalService,
		SiteNavigationMenuLocalService siteNavigationMenuLocalService,
		UserLocalService userLocalService) {

		_companyLocalService = companyLocalService;
		_groupLocalService = groupLocalService;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
		_layoutsImporter = layoutsImporter;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;
		_siteNavigationMenuItemLocalService =
			siteNavigationMenuItemLocalService;
		_siteNavigationMenuLocalService = siteNavigationMenuLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private void _addOrUpdatePlanningNavigationMenuItems(
			Group group, Layout tasksLayout, ServiceContext serviceContext)
		throws Exception {

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.
				fetchSiteNavigationMenuByExternalReferenceCode(
					"CMSPRIMARYNAVIGATION", group.getGroupId());

		if (siteNavigationMenu == null) {
			return;
		}

		SiteNavigationMenuItem planningSiteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.
				addOrUpdateSiteNavigationMenuItem(
					"CMSPRIMARYNAVIGATION11", serviceContext.getUserId(),
					group.getGroupId(),
					siteNavigationMenu.getSiteNavigationMenuId(), 0,
					SiteNavigationMenuItemTypeConstants.NODE,
					_getNodeTypeSettings(
						serviceContext.getCompanyId(), _getPlanningNameMap()),
					serviceContext);

		_siteNavigationMenuItemLocalService.addOrUpdateSiteNavigationMenuItem(
			"CMSPRIMARYNAVIGATION12", serviceContext.getUserId(),
			group.getGroupId(), siteNavigationMenu.getSiteNavigationMenuId(),
			planningSiteNavigationMenuItem.getSiteNavigationMenuItemId(),
			SiteNavigationMenuItemTypeConstants.LAYOUT,
			_getLayoutTypeSettings(tasksLayout), serviceContext);
	}

	private void _addTasksFragmentEntryLink(Layout layout) throws Exception {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		_layoutsImporter.importPageElement(
			layout.getUserId(), layout, layoutStructure,
			layoutStructure.getMainItemId(),
			JSONUtil.put(
				"definition",
				JSONUtil.put(
					"fragment",
					JSONUtil.put(
						"key",
						ViewTasksJSPSectionFragmentRenderer.class.getName())
				).put(
					"fragmentConfig", JSONFactoryUtil.createJSONObject()
				).put(
					"fragmentFields", JSONFactoryUtil.createJSONArray()
				).put(
					"indexed", true
				)
			).put(
				"type", "Fragment"
			).toString(),
			0, true, segmentsExperienceId);
	}

	private String _getLayoutTypeSettings(Layout layout) {
		return UnicodePropertiesBuilder.put(
			"displayIcon", "paste"
		).put(
			"externalReferenceCode", layout.getExternalReferenceCode()
		).put(
			"privateLayout", String.valueOf(layout.isPrivateLayout())
		).buildString();
	}

	private String _getNodeTypeSettings(
			long companyId, Map<Locale, String> nameMap)
		throws Exception {

		UnicodePropertiesBuilder.UnicodePropertiesWrapper
			unicodePropertiesWrapper = UnicodePropertiesBuilder.put(
				"defaultLanguageId",
				UpgradeProcessUtil.getDefaultLanguageId(companyId));

		for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
			unicodePropertiesWrapper.put(
				"name_" + LocaleUtil.toLanguageId(entry.getKey()),
				entry.getValue());
		}

		return unicodePropertiesWrapper.put(
			"title", StringPool.BLANK
		).buildString();
	}

	private Layout _getOrAddPlanningLayout(
			Group group, ServiceContext serviceContext)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/planning");

		if (layout != null) {
			return layout;
		}

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			group.getDefaultLanguageId());

		layout = _layoutLocalService.addLayout(
			null, serviceContext.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, _getPlanningNameMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			LayoutConstants.TYPE_NODE, StringPool.BLANK, false, false,
			Collections.singletonMap(defaultLocale, "/planning"),
			serviceContext);

		_setResourcePermissions(layout);

		return layout;
	}

	private Layout _getOrAddTasksLayout(
			Group group, Layout planningLayout, ServiceContext serviceContext)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/tasks");

		if (layout != null) {
			return layout;
		}

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			group.getDefaultLanguageId());

		layout = _layoutLocalService.addLayout(
			null, serviceContext.getUserId(), group.getGroupId(), false,
			planningLayout.getLayoutId(), _getTasksNameMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			Collections.emptyMap(), Collections.emptyMap(),
			LayoutConstants.TYPE_CONTENT, StringPool.BLANK, false, false,
			Collections.singletonMap(defaultLocale, "/tasks"), serviceContext);

		_setResourcePermissions(layout);

		Layout draftLayout = layout.fetchDraftLayout();

		_setMasterLayout(group, draftLayout);
		_addTasksFragmentEntryLink(draftLayout);

		_setMasterLayout(group, layout);
		_addTasksFragmentEntryLink(layout);

		_layoutLocalService.updateStatus(
			serviceContext.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);
		_layoutLocalService.updateStatus(
			serviceContext.getUserId(), layout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		return layout;
	}

	private Map<Locale, String> _getPlanningNameMap() {
		Map<Locale, String> nameMap = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			nameMap.put(locale, LanguageUtil.get(locale, "planning"));
		}

		return nameMap;
	}

	private Map<Locale, String> _getTasksNameMap() {
		Map<Locale, String> nameMap = new HashMap<>();

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			nameMap.put(locale, LanguageUtil.get(locale, "tasks"));
		}

		return nameMap;
	}

	private void _setMasterLayout(Group group, Layout layout) throws Exception {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntry(
				group.getGroupId(), "cms-master");

		if (layoutPageTemplateEntry == null) {
			return;
		}

		_layoutLocalService.updateMasterLayoutPageTemplateEntryERC(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layoutPageTemplateEntry.getExternalReferenceCode());
	}

	private void _setResourcePermissions(Layout layout) throws PortalException {
		long companyId = layout.getCompanyId();
		String primKey = String.valueOf(layout.getPlid());

		Role cmsAdministratorRole = _roleLocalService.getRole(
			companyId, RoleConstants.CMS_ADMINISTRATOR);

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, primKey,
			cmsAdministratorRole.getRoleId(), new String[] {"VIEW"});

		Role guestRole = _roleLocalService.getRole(
			companyId, RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, primKey, guestRole.getRoleId(),
			new String[0]);

		Role siteMemberRole = _roleLocalService.getRole(
			companyId, RoleConstants.SITE_MEMBER);

		_resourcePermissionLocalService.setResourcePermissions(
			companyId, Layout.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, primKey,
			siteMemberRole.getRoleId(), new String[] {"VIEW"});
	}

	private void _upgradeCompany(long companyId) throws Exception {
		Group group = _groupLocalService.fetchGroup(
			companyId, GroupConstants.CMS);

		if (group == null) {
			return;
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(_userLocalService.getGuestUserId(companyId));

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try {
			Layout planningLayout = _getOrAddPlanningLayout(
				group, serviceContext);

			Layout tasksLayout = _getOrAddTasksLayout(
				group, planningLayout, serviceContext);

			_addOrUpdatePlanningNavigationMenuItems(
				group, tasksLayout, serviceContext);
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private final CompanyLocalService _companyLocalService;
	private final GroupLocalService _groupLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private final LayoutsImporter _layoutsImporter;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;
	private final SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;
	private final SiteNavigationMenuLocalService
		_siteNavigationMenuLocalService;
	private final UserLocalService _userLocalService;

}