/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.constants.PublicationRoleConstants;
import com.liferay.change.tracking.model.CTComment;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/delete_ct_comment"
	},
	service = MVCResourceCommand.class
)
public class DeleteCTCommentMVCResourceCommand
	extends GetCTCommentsMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ctCommentId = ParamUtil.getLong(resourceRequest, "ctCommentId");

		CTComment ctComment = ctCommentLocalService.getCTComment(ctCommentId);

		if (!_hasAdminRole(themeDisplay.getPermissionChecker()) &&
			(ctComment.getUserId() != themeDisplay.getUserId())) {

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"errorMessage",
					_language.get(
						themeDisplay.getLocale(),
						"you-do-not-have-permission-to-delete-this-item")));

			return;
		}

		ctCommentLocalService.deleteCTComment(ctCommentId);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			getCTCommentsJSONObject(resourceRequest));
	}

	private boolean _hasAdminRole(PermissionChecker permissionChecker) {
		if (permissionChecker.isCompanyAdmin()) {
			return true;
		}

		Role publicationAdministratorRole = _roleLocalService.fetchRole(
			permissionChecker.getCompanyId(),
			PublicationRoleConstants.NAME_ADMIN);

		if ((publicationAdministratorRole != null) &&
			_roleLocalService.hasUserRole(
				permissionChecker.getUserId(),
				publicationAdministratorRole.getRoleId())) {

			return true;
		}

		return false;
	}

	@Reference
	private Language _language;

	@Reference
	private RoleLocalService _roleLocalService;

}