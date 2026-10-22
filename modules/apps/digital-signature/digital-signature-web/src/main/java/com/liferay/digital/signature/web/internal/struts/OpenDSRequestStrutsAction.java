/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.struts;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "path=/digital_signature/open_ds_request",
	service = StrutsAction.class
)
public class OpenDSRequestStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			httpServletResponse.sendRedirect(
				HttpComponentsUtil.addParameter(
					_portal.getPathMain() + "/portal/login", "redirect",
					_portal.getCurrentCompleteURL(httpServletRequest)));

			return null;
		}

		DSRequest dsRequest = _dsRequestManager.fetchDSRequest(
			themeDisplay.getCompanyId(),
			ParamUtil.getLong(httpServletRequest, "dsRequestId"));

		if ((dsRequest == null) ||
			!ListUtil.exists(
				dsRequest.getDSRequestRecipients(),
				dsRequestRecipient ->
					dsRequestRecipient.getUserId() ==
						themeDisplay.getUserId())) {

			_portal.sendError(
				HttpServletResponse.SC_NOT_FOUND, new NoSuchModelException(),
				httpServletRequest, httpServletResponse);

			return null;
		}

		Layout layout = _fetchLayout(
			themeDisplay.getCompanyId(), dsRequest.getSiteGroupId(),
			themeDisplay.getPermissionChecker());

		if (layout == null) {
			_portal.sendError(
				HttpServletResponse.SC_NOT_FOUND, new NoSuchLayoutException(),
				httpServletRequest, httpServletResponse);

			return null;
		}

		String signURL = StringBundler.concat(
			_portal.getLayoutFullURL(layout, themeDisplay),
			"/-/digital_signature/sign/", dsRequest.getDSRequestId());

		String windowState = WindowState.MAXIMIZED.toString();

		if (LiferayWindowState.isPopUp(httpServletRequest)) {
			String portletNamespace = _portal.getPortletNamespace(
				DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE);

			String backURL = _portal.escapeRedirect(
				ParamUtil.getString(
					httpServletRequest, portletNamespace + "backURL"));

			if (Validator.isNotNull(backURL)) {
				signURL = HttpComponentsUtil.setParameter(
					signURL, portletNamespace + "backURL", backURL);
			}

			windowState = LiferayWindowState.POP_UP.toString();
		}

		httpServletResponse.sendRedirect(
			HttpComponentsUtil.setParameter(signURL, "p_p_state", windowState));

		return null;
	}

	private Layout _fetchFirstLayout(long groupId, boolean privateLayout)
		throws Exception {

		for (Layout layout :
				_layoutService.getLayouts(
					groupId, privateLayout,
					LayoutConstants.DEFAULT_PARENT_LAYOUT_ID)) {

			if (!layout.isHidden() && layout.isPublished() &&
				(layout.isTypeContent() || layout.isTypePortlet())) {

				return layout;
			}
		}

		return null;
	}

	private Layout _fetchLayout(
			long companyId, long groupId, PermissionChecker permissionChecker)
		throws Exception {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			group = _groupLocalService.getGroup(
				companyId, GroupConstants.GUEST);
		}

		long plid = _portal.getPlidFromPortletId(
			group.getGroupId(),
			DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE);

		if (plid != LayoutConstants.DEFAULT_PLID) {
			Layout layout = _layoutLocalService.getLayout(plid);

			if (LayoutPermissionUtil.contains(
					permissionChecker, layout, ActionKeys.VIEW)) {

				return layout;
			}
		}

		Layout layout = _fetchFirstLayout(group.getGroupId(), false);

		if (layout == null) {
			layout = _fetchFirstLayout(group.getGroupId(), true);
		}

		if (layout != null) {
			return layout;
		}

		Group guestGroup = _groupLocalService.getGroup(
			companyId, GroupConstants.GUEST);

		return _layoutLocalService.fetchFirstLayout(
			guestGroup.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, false);
	}

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

}