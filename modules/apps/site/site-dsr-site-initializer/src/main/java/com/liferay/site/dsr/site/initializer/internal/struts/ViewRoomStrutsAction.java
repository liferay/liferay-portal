/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.struts;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(property = "path=/dsr/view_room", service = StrutsAction.class)
public class ViewRoomStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = _groupLocalService.getGroup(
			ParamUtil.getLong(httpServletRequest, "siteId"));

		if (!GroupPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), group, ActionKeys.VIEW)) {

			SessionErrors.add(
				httpServletRequest,
				PrincipalException.MustHavePermission.class);

			String redirect = httpServletRequest.getHeader(HttpHeaders.REFERER);

			if (Validator.isNull(redirect)) {
				redirect = themeDisplay.getURLHome();
			}

			httpServletResponse.sendRedirect(redirect);

			return null;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", group.getCompanyId());

		if (!Objects.equals(
				group.getClassName(), objectDefinition.getClassName())) {

			return null;
		}

		String groupFriendlyURL = _portal.getGroupFriendlyURL(
			group.getPublicLayoutSet(), themeDisplay, false, false);

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			group.getClassPK());

		if (objectEntry != null) {
			int roomStatus = MapUtil.getInteger(
				objectEntry.getValues(), "roomStatus");

			if (roomStatus == WorkflowConstants.STATUS_INACTIVE) {
				PermissionChecker permissionChecker =
					themeDisplay.getPermissionChecker();

				if (!permissionChecker.isCompanyAdmin() &&
					!permissionChecker.isGroupOwner(group.getGroupId())) {

					SessionErrors.add(
						httpServletRequest,
						PrincipalException.MustHavePermission.class);

					String redirect = _portal.escapeRedirect(
						httpServletRequest.getHeader(HttpHeaders.REFERER));

					if (Validator.isNull(redirect)) {
						redirect = themeDisplay.getURLHome();
					}

					httpServletResponse.sendRedirect(redirect);

					return null;
				}
			}
		}

		if (Objects.equals(
				ParamUtil.getString(httpServletRequest, "mode", "view"),
				"edit")) {

			httpServletResponse.sendRedirect(
				StringBundler.concat(
					groupFriendlyURL, "?p_l_back_url=", groupFriendlyURL,
					"&p_l_mode=edit"));
		}
		else {
			httpServletResponse.sendRedirect(groupFriendlyURL);
		}

		return null;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

}