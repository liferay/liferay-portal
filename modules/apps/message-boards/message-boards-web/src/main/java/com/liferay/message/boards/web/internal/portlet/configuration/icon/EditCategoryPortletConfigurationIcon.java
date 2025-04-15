/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.configuration.icon;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.web.internal.portlet.action.ActionUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

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
		"path=/message_boards/view_category"
	},
	service = PortletConfigurationIcon.class
)
public class EditCategoryPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "edit");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, MBPortletKeys.MESSAGE_BOARDS_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/message_boards/edit_category"
		).setRedirect(
			_portal.getCurrentURL(portletRequest)
		).buildPortletURL();

		MBCategory category = null;

		try {
			category = ActionUtil.getCategory(portletRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}

		portletURL.setParameter(
			"mbCategoryId", String.valueOf(getCategoryId(category)));

		return portletURL.toString();
	}

	@Override
	public double getWeight() {
		return 104;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		try {
			MBCategory category = ActionUtil.getCategory(portletRequest);

			if (category.getCategoryId() ==
					MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

				return false;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (_categoryModelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), category,
					ActionKeys.UPDATE)) {

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

	private static final Log _log = LogFactoryUtil.getLog(
		EditCategoryPortletConfigurationIcon.class);

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}