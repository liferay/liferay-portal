/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.configuration.icon;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationApplicationType;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = PortletConfigurationIcon.class)
public class PortletPermissionsPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "permissions");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			return _generatePermissionURL(portletRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public double getWeight() {
		return 13.0;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(portletRequest));

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode", Constants.VIEW);

		if (!Objects.equals(layoutMode, Constants.EDIT)) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String portletId = portletDisplay.getId();

		if (Validator.isNotNull(portletDisplay.getPortletResource())) {
			portletId = portletDisplay.getPortletResource();
		}

		boolean showPermissionsIcon = false;

		Layout layout = themeDisplay.getLayout();

		Group group = themeDisplay.getScopeGroup();

		if (_STAGING_LIVE_GROUP_LOCKING_ENABLED || !group.hasStagingGroup()) {
			try {
				if (PortletPermissionUtil.contains(
						themeDisplay.getPermissionChecker(), layout, portletId,
						ActionKeys.PERMISSIONS)) {

					showPermissionsIcon = true;
				}
			}
			catch (PortalException portalException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				showPermissionsIcon = false;
			}
		}

		if (layout.isEmbeddedPersonalApplication() ||
			layout.isLayoutPrototypeLinkActive() ||
			layout.isTypeControlPanel()) {

			showPermissionsIcon = false;
		}

		return showPermissionsIcon;
	}

	@Override
	public boolean isShowInEditMode(PortletRequest portletRequest) {
		return true;
	}

	@Override
	public boolean isUseDialog() {
		return true;
	}

	private String _generatePermissionURL(PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				portletRequest,
				PortletConfigurationApplicationType.PortletConfiguration.
					CLASS_NAME,
				PortletProvider.Action.VIEW)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setPortletResource(
			portletDisplay.getId()
		).setParameter(
			"portletConfiguration", true
		).setParameter(
			"resourcePrimKey",
			PortletPermissionUtil.getPrimaryKey(
				themeDisplay.getPlid(), portletDisplay.getId())
		).setParameter(
			"returnToFullPageURL",
			ParamUtil.getString(portletRequest, "returnToFullPageURL")
		).setWindowState(
			LiferayWindowState.POP_UP
		).buildString();
	}

	private static final boolean _STAGING_LIVE_GROUP_LOCKING_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.STAGING_LIVE_GROUP_LOCKING_ENABLED));

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPermissionsPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}