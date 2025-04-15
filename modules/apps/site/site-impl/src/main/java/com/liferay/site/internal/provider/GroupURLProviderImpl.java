/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.provider;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.constants.SiteWebKeys;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = GroupURLProvider.class)
public class GroupURLProviderImpl implements GroupURLProvider {

	@Override
	public String getGroupAdministrationURL(
		Group group, PortletRequest portletRequest) {

		PanelCategoryHelper panelCategoryHelper = new PanelCategoryHelper(
			_panelAppRegistry);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletId = panelCategoryHelper.getFirstPortletId(
			PanelCategoryKeys.SITE_ADMINISTRATION,
			themeDisplay.getPermissionChecker(), group);

		if (Validator.isNotNull(portletId)) {
			PortletURL groupAdministrationURL =
				_portal.getControlPanelPortletURL(
					portletRequest, group, portletId, 0, 0,
					PortletRequest.RENDER_PHASE);

			if (groupAdministrationURL != null) {
				return groupAdministrationURL.toString();
			}
		}

		return null;
	}

	@Override
	public String getGroupLayoutsURL(
		Group group, boolean privateLayout, PortletRequest portletRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String groupDisplayURL = group.getDisplayURL(
			themeDisplay, privateLayout);

		if (Validator.isNotNull(groupDisplayURL)) {
			return groupDisplayURL;
		}

		return null;
	}

	@Override
	public String getGroupURL(Group group, PortletRequest portletRequest) {
		return getGroupURL(group, portletRequest, true);
	}

	@Override
	public String getLiveGroupURL(Group group, PortletRequest portletRequest) {
		return getGroupURL(group, portletRequest, false);
	}

	protected String getGroupURL(
		Group group, PortletRequest portletRequest,
		boolean includeStagingGroup) {

		if (group.isDepot()) {
			String depotDashboardGroupURL = _getDepotDashboardGroupURL(
				group, portletRequest);

			if (depotDashboardGroupURL != null) {
				return depotDashboardGroupURL;
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String groupDisplayURL = group.getDisplayURL(
			themeDisplay, false,
			GetterUtil.getBoolean(
				portletRequest.getAttribute(
					SiteWebKeys.GROUP_URL_PROVIDER_CONTROL_PANEL)));

		if (Validator.isNotNull(groupDisplayURL)) {
			return HttpComponentsUtil.removeParameter(
				groupDisplayURL, "p_p_id");
		}

		groupDisplayURL = group.getDisplayURL(themeDisplay, true);

		if (Validator.isNotNull(groupDisplayURL)) {
			return HttpComponentsUtil.removeParameter(
				groupDisplayURL, "p_p_id");
		}

		if (includeStagingGroup && group.hasStagingGroup()) {
			try {
				if (GroupPermissionUtil.contains(
						themeDisplay.getPermissionChecker(), group,
						ActionKeys.VIEW_STAGING)) {

					return getGroupURL(group.getStagingGroup(), portletRequest);
				}
			}
			catch (PortalException portalException) {
				_log.error(
					"Unable to check permission on group " + group.getGroupId(),
					portalException);
			}
		}

		return getGroupAdministrationURL(group, portletRequest);
	}

	private String _getDepotDashboardGroupURL(
		Group group, PortletRequest portletRequest) {

		try {
			DepotEntryLocalService depotEntryLocalService =
				_depotEntryLocalServiceSnapshot.get();

			if (depotEntryLocalService == null) {
				return null;
			}

			DepotEntry depotEntry = depotEntryLocalService.getGroupDepotEntry(
				group.getGroupId());

			return PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					portletRequest, group, _DEPOT_ADMIN_PORTLET_ID, 0, 0,
					PortletRequest.RENDER_PHASE)
			).setMVCRenderCommandName(
				"/depot/view_depot_dashboard"
			).setParameter(
				"depotEntryId", depotEntry.getDepotEntryId()
			).buildString();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	private static final String _DEPOT_ADMIN_PORTLET_ID =
		"com_liferay_depot_web_portlet_DepotAdminPortlet";

	private static final Log _log = LogFactoryUtil.getLog(
		GroupURLProviderImpl.class);

	private static final Snapshot<DepotEntryLocalService>
		_depotEntryLocalServiceSnapshot = new Snapshot<>(
			GroupURLProviderImpl.class, DepotEntryLocalService.class, null,
			true);

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private Portal _portal;

}