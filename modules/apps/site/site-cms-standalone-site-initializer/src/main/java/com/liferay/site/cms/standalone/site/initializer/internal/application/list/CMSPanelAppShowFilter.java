/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.application.list;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppShowFilter;
import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.expando.constants.ExpandoPortletKeys;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.locked.items.constants.LockedItemsPortletKeys;
import com.liferay.marketplace.constants.MarketplaceStorePortletKeys;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.notification.constants.NotificationPortletKeys;
import com.liferay.on.demand.admin.constants.OnDemandAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.push.notifications.constants.PushNotificationsPortletKeys;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.site.memberships.constants.SiteMembershipsPortletKeys;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.staging.constants.StagingProcessesPortletKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.translation.constants.TranslationPortletKeys;
import com.liferay.trash.constants.TrashPortletKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;
import com.liferay.wiki.constants.WikiPortletKeys;

import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez Álvarez
 */
@Component(service = PanelAppShowFilter.class)
public class CMSPanelAppShowFilter implements PanelAppShowFilter {

	@Override
	public boolean isShow(
			PanelApp panelApp, PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (_hiddenPortletIds.contains(panelApp.getPortletId())) {
			return false;
		}

		return panelApp.isShow(permissionChecker, group);
	}

	private static final Set<String> _hiddenPortletIds = SetUtil.fromArray(
		AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
		AssetListPortletKeys.ASSET_LIST,
		AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN,
		BlogsPortletKeys.BLOGS_ADMIN, BookmarksPortletKeys.BOOKMARKS_ADMIN,
		ConfigurationAdminPortletKeys.SITE_SETTINGS, CTPortletKeys.PUBLICATIONS,
		DDLPortletKeys.DYNAMIC_DATA_LISTS,
		DDMPortletKeys.DYNAMIC_DATA_MAPPING_DATA_PROVIDER,
		DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		DepotPortletKeys.DEPOT_ADMIN, DepotPortletKeys.DEPOT_SETTINGS,
		DigitalSignaturePortletKeys.DIGITAL_SIGNATURE,
		DispatchPortletKeys.DISPATCH, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		ExpandoPortletKeys.EXPANDO, ExportImportPortletKeys.EXPORT,
		ExportImportPortletKeys.IMPORT, FragmentPortletKeys.FRAGMENT,
		JournalPortletKeys.JOURNAL, KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		LayoutAdminPortletKeys.GROUP_PAGES,
		LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE,
		LayoutSetPrototypePortletKeys.SITE_TEMPLATE_SETTINGS,
		LockedItemsPortletKeys.LOCKED_ITEMS,
		MarketplaceStorePortletKeys.MARKETPLACE_PURCHASED,
		MarketplaceStorePortletKeys.MARKETPLACE_STORE,
		MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		NotificationPortletKeys.NOTIFICATION_QUEUE_ENTRIES,
		NotificationPortletKeys.NOTIFICATION_TEMPLATES,
		OnDemandAdminPortletKeys.ON_DEMAND_ADMIN,
		PushNotificationsPortletKeys.PUSH_NOTIFICATIONS,
		SegmentsPortletKeys.SEGMENTS,
		SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN,
		SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		StagingProcessesPortletKeys.STAGING_PROCESSES,
		StyleBookPortletKeys.STYLE_BOOK, TemplatePortletKeys.TEMPLATE,
		TranslationPortletKeys.TRANSLATION, TrashPortletKeys.TRASH,
		UsersAdminPortletKeys.SERVICE_ACCOUNTS, WikiPortletKeys.WIKI_ADMIN,
		WorkflowPortletKeys.SITE_ADMINISTRATION_WORKFLOW,
		"com_liferay_address_web_internal_portlet_" +
			"CountriesManagementAdminPortlet",
		"com_liferay_akismet_web_portlet_ModerationPortlet",
		"com_liferay_dynamic_data_mapping_web_portlet_" +
			"PortletDisplayTemplatePortlet",
		"com_liferay_gogo_shell_web_internal_portlet_GogoShellPortlet",
		"com_liferay_layout_locked_layouts_web_internal_portlet_" +
			"LockedLayoutsPortlet",
		"com_liferay_layout_portlets_web_internal_portlet_" +
			"LayoutPortletsPortlet",
		"com_liferay_license_manager_web_portlet_LicenseManagerPortlet",
		"com_liferay_marketplace_app_manager_web_portlet_" +
			"MarketplaceAppManagerPortlet",
		"com_liferay_monitoring_web_portlet_MonitoringPortlet",
		"com_liferay_plugins_admin_web_portlet_PluginsAdminPortlet",
		"com_liferay_portal_instances_web_portlet_PortalInstancesPortlet",
		"com_liferay_portal_language_override_web_internal_portlet_PLOPortlet",
		"com_liferay_portal_reports_engine_console_web_admin_portlet_" +
			"AdminPortlet",
		"com_liferay_portal_workflow_kaleo_forms_web_portlet_" +
			"KaleoFormsAdminPortlet",
		"com_liferay_redirect_web_internal_portlet_RedirectPortlet",
		"com_liferay_search_experiences_web_internal_blueprint_admin_portlet_" +
			"SXPBlueprintAdminPortlet",
		"com_liferay_server_admin_web_portlet_ServerAdminPortlet",
		"com_liferay_site_admin_web_portlet_SiteAdminPortlet",
		"com_liferay_site_initializer_extender_web_SiteInitializerPortlet",
		"com_liferay_site_teams_web_portlet_SiteTeamsPortlet");

}