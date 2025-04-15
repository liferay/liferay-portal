/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.configuration.icon;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.web.internal.portlet.action.ActionUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"path=/message_boards/view_message"
	},
	service = PortletConfigurationIcon.class
)
public class DeleteThreadPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "delete");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		PortletURL deleteURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, MBPortletKeys.MESSAGE_BOARDS_ADMIN,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/message_boards/delete_thread"
		).setCMD(
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				String cmd = Constants.DELETE;

				if (isTrashEnabled(themeDisplay.getScopeGroupId())) {
					cmd = Constants.MOVE_TO_TRASH;
				}

				return cmd;
			}
		).buildPortletURL();

		try {
			MBCategory category = ActionUtil.getCategory(portletRequest);

			PortletURL parentCategoryURL = PortletURLFactoryUtil.create(
				portletRequest, MBPortletKeys.MESSAGE_BOARDS_ADMIN,
				PortletRequest.RENDER_PHASE);

			long categoryId = getCategoryId(category);

			if (categoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
				parentCategoryURL.setParameter(
					"mvcRenderCommandName", "/message_boards/view");
			}
			else {
				parentCategoryURL.setParameter(
					"mvcRenderCommandName", "/message_boards/view_category");
				parentCategoryURL.setParameter(
					"mbCategoryId", String.valueOf(categoryId));
			}

			deleteURL.setParameter("redirect", parentCategoryURL.toString());

			MBMessage message = ActionUtil.getMessage(portletRequest);

			deleteURL.setParameter(
				"threadId", String.valueOf(message.getThreadId()));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}

		return deleteURL.toString();
	}

	@Override
	public double getWeight() {
		return 100;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		try {
			MBMessage message = ActionUtil.getMessage(portletRequest);

			MBThread thread = message.getThread();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (_messageModelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), message,
					ActionKeys.DELETE) &&
				!thread.isLocked()) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	protected long getCategoryId(MBCategory category) {
		long categoryId = MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;

		if (category != null) {
			categoryId = category.getCategoryId();
		}

		return categoryId;
	}

	protected boolean isTrashEnabled(long groupId) {
		try {
			if (_trashHelper.isTrashEnabled(groupId)) {
				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteThreadPortletConfigurationIcon.class);

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBMessage)"
	)
	private ModelResourcePermission<MBMessage> _messageModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}