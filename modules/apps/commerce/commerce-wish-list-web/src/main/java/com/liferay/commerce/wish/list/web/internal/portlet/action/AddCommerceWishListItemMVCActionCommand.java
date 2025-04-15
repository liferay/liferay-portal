/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.web.internal.portlet.action;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.commerce.wish.list.constants.CommerceWishListPortletKeys;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.model.CommerceWishListItem;
import com.liferay.commerce.wish.list.service.CommerceWishListItemService;
import com.liferay.commerce.wish.list.service.CommerceWishListService;
import com.liferay.commerce.wish.list.util.CommerceWishListHttpHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceWishListPortletKeys.COMMERCE_WISH_LIST_CONTENT,
		"mvc.command.name=/commerce_wish_list_content/add_commerce_wish_list_item"
	},
	service = MVCActionCommand.class
)
public class AddCommerceWishListItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		long cpDefinitionId = ParamUtil.getLong(
			actionRequest, "cpDefinitionId");
		long cpInstanceId = ParamUtil.getLong(actionRequest, "cpInstanceId");
		String formFieldValues = ParamUtil.getString(
			actionRequest, "formFieldValues");

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		try {
			CPInstance cpInstance = _cpInstanceLocalService.fetchCPInstance(
				cpInstanceId);

			String cpInstanceUuid = StringPool.BLANK;

			if (cpInstance != null) {
				cpInstanceUuid = cpInstance.getCPInstanceUuid();
			}

			CommerceWishList commerceWishList =
				_commerceWishListHttpHelper.getCurrentCommerceWishList(
					httpServletRequest, httpServletResponse);

			if (commerceWishList == null) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				commerceWishList = _commerceWishListService.addCommerceWishList(
					themeDisplay.getScopeGroupId(),
					_language.get(themeDisplay.getLocale(), "default"), true);
			}

			CommerceWishListItem commerceWishListItem =
				_commerceWishListItemService.addCommerceWishListItem(
					CommerceUtil.getCommerceAccountId(
						(CommerceContext)httpServletRequest.getAttribute(
							CommerceWebKeys.COMMERCE_CONTEXT)),
					commerceWishList.getCommerceWishListId(), cpInstanceUuid,
					cpDefinitionId, formFieldValues);

			jsonObject.put(
				"commerceWishListItemId",
				commerceWishListItem.getCommerceWishListItemId()
			).put(
				"commerceWishListItemsCount",
				_commerceWishListItemService.getCommerceWishListItemsCount(
					commerceWishList.getCommerceWishListId())
			).put(
				"success", true
			);
		}
		catch (Exception exception) {
			_log.error(exception);

			jsonObject.put(
				"error", exception.getMessage()
			).put(
				"success", false
			);
		}

		hideDefaultSuccessMessage(actionRequest);

		_writeJSON(httpServletResponse, jsonObject);
	}

	private void _writeJSON(
			HttpServletResponse httpServletResponse, JSONObject jsonObject)
		throws Exception {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(httpServletResponse, jsonObject.toString());

		httpServletResponse.flushBuffer();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddCommerceWishListItemMVCActionCommand.class);

	@Reference
	private CommerceWishListHttpHelper _commerceWishListHttpHelper;

	@Reference
	private CommerceWishListItemService _commerceWishListItemService;

	@Reference
	private CommerceWishListService _commerceWishListService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}