/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.web.internal.portlet.action;

import com.liferay.commerce.wish.list.constants.CommerceWishListPortletKeys;
import com.liferay.commerce.wish.list.exception.NoSuchWishListItemException;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceWishListPortletKeys.COMMERCE_WISH_LIST_CONTENT,
		"mvc.command.name=/commerce_wish_list_content/edit_commerce_wish_list_item"
	},
	service = MVCActionCommand.class
)
public class EditCommerceWishListItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteCommerceWishListItems(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchWishListItemException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	private void _deleteCommerceWishListItems(ActionRequest actionRequest)
		throws PortalException {

		long[] deleteCommerceWishListItemIds = null;

		long commerceWishListItemId = ParamUtil.getLong(
			actionRequest, "commerceWishListItemId");

		if (commerceWishListItemId > 0) {
			deleteCommerceWishListItemIds = new long[] {commerceWishListItemId};
		}
		else {
			deleteCommerceWishListItemIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommerceWishListItemIds"),
				0L);
		}

		for (long deleteCommerceWishListItemId :
				deleteCommerceWishListItemIds) {

			_commerceWishListItemService.deleteCommerceWishListItem(
				deleteCommerceWishListItemId);
		}
	}

	@Reference
	private CommerceWishListItemService _commerceWishListItemService;

}