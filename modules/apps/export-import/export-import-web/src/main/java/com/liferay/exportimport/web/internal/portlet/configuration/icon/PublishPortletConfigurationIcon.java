/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.configuration.icon;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.constants.StagingConfigurationPortletKeys;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Raposo
 */
@Component(service = PortletConfigurationIcon.class)
public class PublishPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "staging";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "staging");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			return PortletURLBuilder.create(
				PortletProviderUtil.getPortletURL(
					portletRequest, ExportImportConfiguration.class.getName(),
					PortletProvider.Action.VIEW)
			).setMVCPath(
				"/revamp/publish/view_publish.jsp"
			).setRedirect(
				themeDisplay.getURLCurrent()
			).setPortletResource(
				portletDisplay.getId()
			).setParameter(
				"portletConfiguration", true
			).setParameter(
				"resourcePrimKey",
				() -> {
					Portlet portlet = (Portlet)portletRequest.getAttribute(
						WebKeys.RENDER_PORTLET);

					return PortletPermissionUtil.getPrimaryKey(
						themeDisplay.getPlid(), portlet.getPortletId());
				}
			).setParameter(
				"settingsScope",
				() -> {
					String settingsScope = (String)portletRequest.getAttribute(
						WebKeys.SETTINGS_SCOPE);

					return ParamUtil.get(
						portletRequest, "settingsScope", settingsScope);
				}
			).setWindowState(
				LiferayWindowState.MAXIMIZED
			).buildString();
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return null;
		}
	}

	@Override
	public double getWeight() {
		return 16.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-96689")) {

			return false;
		}

		Group siteGroup = themeDisplay.getSiteGroup();

		if (siteGroup.hasLocalOrRemoteStagingGroup() &&
			!PropsValues.STAGING_LIVE_GROUP_REMOTE_STAGING_ENABLED) {

			return false;
		}

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String rootPortletId = portletDisplay.getRootPortletId();

		if (rootPortletId.equals(ExportImportPortletKeys.EXPORT) ||
			rootPortletId.equals(ExportImportPortletKeys.EXPORT_IMPORT) ||
			rootPortletId.equals(ExportImportPortletKeys.IMPORT) ||
			rootPortletId.equals(
				StagingConfigurationPortletKeys.STAGING_CONFIGURATION) ||
			rootPortletId.equals(
				StagingProcessesPortletKeys.STAGING_PROCESSES) ||
			!portletDisplay.isShowStagingIcon()) {

			return false;
		}

		Portlet portlet = _portletLocalService.getPortletById(
			portletDisplay.getId());

		PortletDataHandler portletDataHandler =
			portlet.getPortletDataHandlerInstance();

		if ((portletDataHandler == null) ||
			!portletDataHandler.isConfigurationEnabled()) {

			return false;
		}

		try {
			return GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroup(), ActionKeys.PUBLISH_PORTLET_INFO);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private PortletLocalService _portletLocalService;

}