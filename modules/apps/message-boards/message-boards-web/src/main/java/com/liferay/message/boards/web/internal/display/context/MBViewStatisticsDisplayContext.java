/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.display.context;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.web.internal.display.MBCategoryDisplay;
import com.liferay.message.boards.web.internal.util.MBUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class MBViewStatisticsDisplayContext {

	public MBViewStatisticsDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public MBCategoryDisplay getMBCategoryDisplay() {
		return new MBCategoryDisplay(
			_themeDisplay.getScopeGroupId(), _getCategoryId());
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/message_boards/view_statistics"
		).setParameter(
			"mbCategoryId", _getCategoryId()
		).buildPortletURL();
	}

	public boolean isMBAdmin() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return Objects.equals(
			portletDisplay.getPortletName(),
			MBPortletKeys.MESSAGE_BOARDS_ADMIN);
	}

	private long _getCategoryId() {
		if (_categoryId != null) {
			return _categoryId;
		}

		if (!isMBAdmin()) {
			MBCategory category = (MBCategory)_renderRequest.getAttribute(
				WebKeys.MESSAGE_BOARDS_CATEGORY);

			_categoryId = MBUtil.getCategoryId(_renderRequest, category);
		}
		else {
			_categoryId = GetterUtil.getLong(
				_renderRequest.getAttribute("view.jsp-categoryId"));
		}

		return _categoryId;
	}

	private Long _categoryId;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}