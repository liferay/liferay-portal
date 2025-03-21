/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.processes.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.staging.constants.StagingProcessesPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = "jakarta.portlet.name=" + StagingProcessesPortletKeys.STAGING_PROCESSES,
	service = PortletConfigurationIcon.class
)
public class PublishTemplatesConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "publish-templates");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, StagingProcessesPortletKeys.STAGING_PROCESSES,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/publish_templates/view_publish_configurations.jsp"
		).setRedirect(
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return themeDisplay.getURLCurrent();
			}
		).buildString();
	}

	@Override
	public double getWeight() {
		return 102.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Group scopeGroup = themeDisplay.getScopeGroup();

		try {
			if (!GroupPermissionUtil.contains(
					themeDisplay.getPermissionChecker(), scopeGroup,
					ActionKeys.PUBLISH_STAGING)) {

				return false;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}

		if ((scopeGroup != null) &&
			(scopeGroup.hasStagingGroup() ||
			 (scopeGroup.hasRemoteStagingGroup() &&
			  !scopeGroup.isStagedRemotely()) ||
			 (!scopeGroup.isStaged() && !scopeGroup.isStagingGroup()))) {

			return false;
		}

		User user = themeDisplay.getUser();

		return !user.isGuestUser();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PublishTemplatesConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}