/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.MBCategoryLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/select_category"
	},
	service = MVCRenderCommand.class
)
public class SelectCategoryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		MBCategory mbCategory = _getMBCategory(renderRequest);

		if (mbCategory != null) {
			renderRequest.setAttribute(
				WebKeys.MESSAGE_BOARDS_CATEGORY, mbCategory);
		}

		return "/message_boards/select_category.jsp";
	}

	private MBCategory _getMBCategory(RenderRequest renderRequest) {
		long mbCategoryId = ParamUtil.getLong(renderRequest, "mbCategoryId");

		if (mbCategoryId != MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			return _mbCategoryLocalService.fetchMBCategory(mbCategoryId);
		}

		String mbCategoryExternalReferenceCode = ParamUtil.getString(
			renderRequest, "mbCategoryExternalReferenceCode");

		if (Validator.isBlank(mbCategoryExternalReferenceCode)) {
			return null;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _mbCategoryLocalService.fetchMBCategoryByExternalReferenceCode(
			mbCategoryExternalReferenceCode, themeDisplay.getScopeGroupId());
	}

	@Reference
	private MBCategoryLocalService _mbCategoryLocalService;

}