/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.message.boards.service.MBCategoryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/move_category"
	},
	service = MVCActionCommand.class
)
public class MoveCategoryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_moveCategory(actionRequest);

			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			if (Validator.isNotNull(redirect)) {
				actionResponse.sendRedirect(redirect);
			}
		}
		catch (PrincipalException principalException) {
			SessionErrors.add(actionRequest, principalException.getClass());
		}
	}

	private void _moveCategory(ActionRequest actionRequest) throws Exception {
		long categoryId = ParamUtil.getLong(actionRequest, "mbCategoryId");

		long parentCategoryId = ParamUtil.getLong(
			actionRequest, "parentCategoryId");

		boolean mergeWithParentCategory = ParamUtil.getBoolean(
			actionRequest, "mergeWithParentCategory");

		_mbCategoryService.moveCategory(
			categoryId, parentCategoryId, mergeWithParentCategory);
	}

	@Reference
	private MBCategoryService _mbCategoryService;

	@Reference
	private Portal _portal;

}