/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.display.context;

import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.oauth2.provider.constants.OAuth2ProviderActionKeys;
import com.liferay.oauth2.provider.constants.OAuth2ProviderConstants;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequest;

/**
 * @author Tomas Polesovsky
 */
public abstract class BaseOAuth2PortletDisplayContext {

	public String getDefaultIconURL() {
		return themeDisplay.getPathThemeImages() + "/common/portlet.png";
	}

	public OAuth2Application getOAuth2Application() throws PortalException {
		if (oAuth2Application != null) {
			return oAuth2Application;
		}

		long oAuth2ApplicationId = ParamUtil.getLong(
			portletRequest, "oAuth2ApplicationId");

		if (oAuth2ApplicationId > 0) {
			oAuth2Application = oAuth2ApplicationService.getOAuth2Application(
				oAuth2ApplicationId);
		}

		return oAuth2Application;
	}

	public String getThumbnailURL() throws Exception {
		return getThumbnailURL(getOAuth2Application());
	}

	public String getThumbnailURL(OAuth2Application oAuth2Application)
		throws Exception {

		if (oAuth2Application.getIconFileEntryId() <= 0) {
			return getDefaultIconURL();
		}

		return dlURLHelper.getThumbnailSrc(
			DLAppLocalServiceUtil.getFileEntry(
				oAuth2Application.getIconFileEntryId()),
			themeDisplay);
	}

	public boolean hasAddApplicationPermission() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return permissionChecker.hasPermission(
			0, OAuth2ProviderConstants.RESOURCE_NAME,
			OAuth2ProviderConstants.RESOURCE_NAME,
			OAuth2ProviderActionKeys.ACTION_ADD_APPLICATION);
	}

	public boolean hasAddTrustedApplicationPermission() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return permissionChecker.hasPermission(
			0, OAuth2ProviderConstants.RESOURCE_NAME,
			OAuth2ProviderConstants.RESOURCE_NAME,
			OAuth2ProviderActionKeys.ACTION_ADD_TRUSTED_APPLICATION);
	}

	public boolean hasDeletePermission(OAuth2Application oAuth2Application) {
		return hasPermission(oAuth2Application, ActionKeys.DELETE);
	}

	public boolean hasPermission(
		OAuth2Application oAuth2Application, String actionId) {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker.hasOwnerPermission(
				oAuth2Application.getCompanyId(),
				OAuth2Application.class.getName(),
				oAuth2Application.getOAuth2ApplicationId(),
				oAuth2Application.getUserId(), actionId)) {

			return true;
		}

		return permissionChecker.hasPermission(
			0, OAuth2Application.class.getName(),
			oAuth2Application.getOAuth2ApplicationId(), actionId);
	}

	public boolean hasPermissionsPermission(
		OAuth2Application oAuth2Application) {

		return hasPermission(oAuth2Application, ActionKeys.PERMISSIONS);
	}

	public boolean hasRememberDevicePermission() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return permissionChecker.hasPermission(
			0, OAuth2ProviderConstants.RESOURCE_NAME,
			OAuth2ProviderConstants.RESOURCE_NAME,
			OAuth2ProviderActionKeys.ACTION_ADD_REMEMBER_DEVICE);
	}

	public boolean hasRevokeTokenPermission(
		OAuth2Application oAuth2Application) {

		return hasPermission(
			oAuth2Application, OAuth2ProviderActionKeys.ACTION_REVOKE_TOKEN);
	}

	public boolean hasUpdatePermission(OAuth2Application oAuth2Application) {
		return hasPermission(oAuth2Application, ActionKeys.UPDATE);
	}

	public boolean hasViewGrantedAuthorizationsPermission() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			return PortletPermissionUtil.contains(
				permissionChecker, OAuth2ProviderPortletKeys.OAUTH2_ADMIN,
				OAuth2ProviderActionKeys.ACTION_VIEW_GRANTED_AUTHORIZATIONS);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	protected BaseOAuth2PortletDisplayContext(
		DLURLHelper dlURLHelper,
		OAuth2ApplicationService oAuth2ApplicationService,
		PortletRequest portletRequest, ThemeDisplay themeDisplay) {

		this.dlURLHelper = dlURLHelper;
		this.oAuth2ApplicationService = oAuth2ApplicationService;
		this.portletRequest = portletRequest;
		this.themeDisplay = themeDisplay;
	}

	protected DLURLHelper dlURLHelper;
	protected OAuth2Application oAuth2Application;
	protected OAuth2ApplicationService oAuth2ApplicationService;
	protected PortletRequest portletRequest;
	protected ThemeDisplay themeDisplay;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseOAuth2PortletDisplayContext.class);

}