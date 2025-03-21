/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.exception.NoSuchCategoryException;
import com.liferay.message.boards.exception.NoSuchMessageException;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBMessageDisplay;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBBanLocalServiceUtil;
import com.liferay.message.boards.service.MBCategoryServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.message.boards.service.MBMessageServiceUtil;
import com.liferay.message.boards.service.MBThreadLocalServiceUtil;
import com.liferay.message.boards.web.internal.security.permission.MBResourcePermission;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class ActionUtil {

	public static MBCategory getCategory(HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String mvcRenderCommandName = ParamUtil.getString(
			httpServletRequest, "mvcRenderCommandName");

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (mvcRenderCommandName.equals(
				"/message_boards_admin/view_banned_users") &&
			!MBResourcePermission.contains(
				permissionChecker, themeDisplay.getScopeGroupId(),
				ActionKeys.BAN_USER)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, ActionKeys.BAN_USER);
		}

		MBBanLocalServiceUtil.checkBan(
			themeDisplay.getScopeGroupId(), themeDisplay.getUserId());

		long categoryId = ParamUtil.getLong(httpServletRequest, "mbCategoryId");

		MBCategory category = null;

		if (categoryId > 0) {
			category = MBCategoryServiceUtil.getCategory(categoryId);

			if (category.getGroupId() != themeDisplay.getScopeGroupId()) {
				throw new NoSuchCategoryException(
					StringBundler.concat(
						"Category ", categoryId, " does not belong to group ",
						themeDisplay.getScopeGroupId()));
			}
		}
		else {
			MBResourcePermission.check(
				permissionChecker, themeDisplay.getScopeGroupId(),
				ActionKeys.VIEW);
		}

		return category;
	}

	public static MBCategory getCategory(PortletRequest portletRequest)
		throws Exception {

		return getCategory(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static MBMessage getMessage(HttpServletRequest httpServletRequest)
		throws Exception {

		MBMessage message = null;

		long messageId = ParamUtil.getLong(httpServletRequest, "messageId");

		if (messageId > 0) {
			message = MBMessageServiceUtil.getMessage(messageId);
		}

		if ((message != null) && message.isInTrash()) {
			throw new NoSuchMessageException("{messageId=" + messageId + "}");
		}

		return message;
	}

	public static MBMessage getMessage(PortletRequest portletRequest)
		throws Exception {

		return getMessage(PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static MBMessageDisplay getMessageDisplay(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		MBMessageDisplay messageDisplay = null;

		long messageId = ParamUtil.getLong(httpServletRequest, "messageId");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (permissionChecker.isContentReviewer(
				themeDisplay.getUserId(), themeDisplay.getScopeGroupId())) {

			messageDisplay = MBMessageLocalServiceUtil.getMessageDisplay(
				themeDisplay.getUserId(), messageId,
				WorkflowConstants.STATUS_ANY);
		}
		else {
			messageDisplay = MBMessageServiceUtil.getMessageDisplay(
				messageId, WorkflowConstants.STATUS_APPROVED);
		}

		if (messageDisplay != null) {
			MBMessage message = messageDisplay.getMessage();

			if ((message != null) && message.isInTrash()) {
				throw new NoSuchMessageException(
					"{messageId=" + messageId + "}");
			}
		}

		return messageDisplay;
	}

	public static MBMessageDisplay getMessageDisplay(
			PortletRequest portletRequest)
		throws PortalException {

		return getMessageDisplay(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

	public static MBMessage getThreadMessage(
			HttpServletRequest httpServletRequest)
		throws Exception {

		long threadId = ParamUtil.getLong(httpServletRequest, "threadId");

		MBMessage message = null;

		if (threadId > 0) {
			MBThread thread = MBThreadLocalServiceUtil.getThread(threadId);

			message = MBMessageServiceUtil.getMessage(
				thread.getRootMessageId());
		}

		if ((message != null) && message.isInTrash()) {
			throw new NoSuchMessageException("{threadId=" + threadId + "}");
		}

		return message;
	}

	public static MBMessage getThreadMessage(PortletRequest portletRequest)
		throws Exception {

		return getThreadMessage(
			PortalUtil.getHttpServletRequest(portletRequest));
	}

}