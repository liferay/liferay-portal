/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTComment;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTCollectionPermission;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
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
		"mvc.command.name=/change_tracking/update_ct_comment"
	},
	service = MVCResourceCommand.class
)
public class UpdateCTCommentMVCResourceCommand
	extends GetCTCommentsMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (!StringUtil.equals(resourceRequest.getMethod(), HttpMethods.POST)) {
			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long ctCollectionId = ParamUtil.getLong(
			resourceRequest, "ctCollectionId");

		try {
			AuthTokenUtil.checkCSRFToken(
				_portal.getHttpServletRequest(resourceRequest),
				UpdateCTCommentMVCResourceCommand.class.getName());

			CTCollectionPermission.check(
				themeDisplay.getPermissionChecker(), ctCollectionId,
				ActionKeys.VIEW);
		}
		catch (PrincipalException principalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(principalException);
			}

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"errorMessage",
					_language.get(
						themeDisplay.getLocale(),
						"you-do-not-have-the-required-permissions")));

			return;
		}

		CTComment ctComment = null;

		long ctCommentId = ParamUtil.getLong(resourceRequest, "ctCommentId");

		String value = ParamUtil.getString(resourceRequest, "value");

		if (ctCommentId > 0) {
			CTComment existingCTComment = ctCommentLocalService.getCTComment(
				ctCommentId);

			if (existingCTComment.getUserId() == themeDisplay.getUserId()) {
				ctComment = ctCommentLocalService.updateCTComment(
					ctCommentId, value);
			}
			else {
				JSONPortletResponseUtil.writeJSON(
					resourceRequest, resourceResponse,
					JSONUtil.put(
						"errorMessage",
						_language.get(
							themeDisplay.getLocale(),
							"you-do-not-have-the-required-permissions")));

				return;
			}
		}
		else {
			long ctEntryId = ParamUtil.getLong(resourceRequest, "ctEntryId");

			ctComment = ctCommentLocalService.addCTComment(
				themeDisplay.getUserId(), ctCollectionId, ctEntryId, value);
		}

		JSONObject jsonObject = getCTCommentsJSONObject(resourceRequest);

		jsonObject.put("updatedCommentId", ctComment.getCtCommentId());

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateCTCommentMVCResourceCommand.class);

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}