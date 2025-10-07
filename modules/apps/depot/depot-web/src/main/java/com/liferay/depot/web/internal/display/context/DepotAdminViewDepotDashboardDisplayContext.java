/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.display.context;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.util.PanelCategoryRegistryUtil;
import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.asset.tags.constants.AssetTagsAdminPortletKeys;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.web.internal.frontend.taglib.clay.servlet.taglib.DepotDashboardApplicationNavigationCard;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.NavigationCard;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;
import com.liferay.site.memberships.constants.SiteMembershipsPortletKeys;
import com.liferay.staging.constants.StagingProcessesPortletKeys;
import com.liferay.trash.constants.TrashPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class DepotAdminViewDepotDashboardDisplayContext {

	public DepotAdminViewDepotDashboardDisplayContext(
		Group group, HttpServletRequest httpServletRequest,
		PanelAppRegistry panelAppRegistry, PermissionChecker permissionChecker,
		Portal portal) {

		_group = group;
		_httpServletRequest = httpServletRequest;
		_panelAppRegistry = panelAppRegistry;
		_permissionChecker = permissionChecker;
		_portal = portal;
	}

	public NavigationCard getDepotDashboardApplicationNavigationCard(
		PanelApp panelApp, Locale locale, Boolean small) {

		return new DepotDashboardApplicationNavigationCard(
			_getPortletURL(panelApp), _getIcon(panelApp), small,
			_portal.getPortletTitle(panelApp.getPortletId(), locale));
	}

	public Collection<PanelApp> getPanelApps(PanelCategory panelCategory)
		throws PortalException {

		return _panelAppRegistry.getPanelApps(
			panelCategory.getKey(), _permissionChecker, _group);
	}

	public Iterable<PanelCategory> getPanelCategories() throws PortalException {
		Collection<PanelCategory> panelCategories = new ArrayList<>();

		for (String panelCategoryKey : _PANEL_CATEGORY_KEYS) {
			PanelCategory panelCategory =
				PanelCategoryRegistryUtil.getPanelCategory(panelCategoryKey);

			if ((panelCategory != null) &&
				panelCategory.isShow(_permissionChecker, _group)) {

				panelCategories.add(panelCategory);
			}
		}

		return panelCategories;
	}

	public boolean isPrimaryPanelCategory(PanelCategory panelCategory) {
		return Objects.equals(panelCategory.getKey(), _PANEL_CATEGORY_KEYS[0]);
	}

	private String _getIcon(PanelApp panelApp) {
		return _panelAppIcons.getOrDefault(panelApp.getPortletId(), "cards2");
	}

	private String _getPortletURL(PanelApp panelApp) {
		PortletURL portletURL = _portal.getControlPanelPortletURL(
			_httpServletRequest, _group, panelApp.getPortletId(), 0, 0,
			PortletRequest.RENDER_PHASE);

		return portletURL.toString();
	}

	// Order matters

	private static final String[] _PANEL_CATEGORY_KEYS = {
		PanelCategoryKeys.SITE_ADMINISTRATION_CONTENT,
		PanelCategoryKeys.SITE_ADMINISTRATION_BUILD,
		PanelCategoryKeys.SITE_ADMINISTRATION_CATEGORIZATION,
		PanelCategoryKeys.SITE_ADMINISTRATION_RECYCLE_BIN,
		PanelCategoryKeys.SITE_ADMINISTRATION_MEMBERS,
		PanelCategoryKeys.SITE_ADMINISTRATION_CONFIGURATION,
		PanelCategoryKeys.SITE_ADMINISTRATION_PUBLISHING
	};

	private static final Map<String, String> _panelAppIcons =
		HashMapBuilder.put(
			AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN, "categories"
		).put(
			AssetListPortletKeys.ASSET_LIST, "closed-book"
		).put(
			AssetTagsAdminPortletKeys.ASSET_TAGS_ADMIN, "tag"
		).put(
			DepotPortletKeys.DEPOT_SETTINGS, "cog"
		).put(
			DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, "documents-and-media"
		).put(
			ExportImportPortletKeys.EXPORT, "upload"
		).put(
			ExportImportPortletKeys.IMPORT, "download"
		).put(
			JournalPortletKeys.JOURNAL, "web-content"
		).put(
			SiteMembershipsPortletKeys.SITE_MEMBERSHIPS_ADMIN, "users"
		).put(
			StagingProcessesPortletKeys.STAGING_PROCESSES, "staging"
		).put(
			TrashPortletKeys.TRASH, "trash"
		).put(
			WorkflowPortletKeys.SITE_ADMINISTRATION_WORKFLOW, "workflow"
		).build();

	private final Group _group;
	private final HttpServletRequest _httpServletRequest;
	private final PanelAppRegistry _panelAppRegistry;
	private final PermissionChecker _permissionChecker;
	private final Portal _portal;

}