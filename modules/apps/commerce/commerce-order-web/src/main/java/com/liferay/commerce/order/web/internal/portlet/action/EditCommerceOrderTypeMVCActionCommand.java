/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.exception.NoSuchOrderTypeException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER_TYPE,
		"mvc.command.name=/commerce_order_type/edit_commerce_order_type"
	},
	service = MVCActionCommand.class
)
public class EditCommerceOrderTypeMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				String externalReferenceCode = ParamUtil.getString(
					actionRequest, "externalReferenceCode");
				long commerceOrderTypeId = ParamUtil.getLong(
					actionRequest, "commerceOrderTypeId");

				Map<Locale, String> nameMap = _localization.getLocalizationMap(
					actionRequest, "name");
				Map<Locale, String> descriptionMap =
					_localization.getLocalizationMap(
						actionRequest, "description");
				boolean active = ParamUtil.getBoolean(actionRequest, "active");
				int displayDateMonth = ParamUtil.getInteger(
					actionRequest, "displayDateMonth");
				int displayDateDay = ParamUtil.getInteger(
					actionRequest, "displayDateDay");
				int displayDateYear = ParamUtil.getInteger(
					actionRequest, "displayDateYear");
				int displayDateHour = ParamUtil.getInteger(
					actionRequest, "displayDateHour");

				int displayDateAmPm = ParamUtil.getInteger(
					actionRequest, "displayDateAmPm");

				if (displayDateAmPm == Calendar.PM) {
					displayDateHour += 12;
				}

				int displayDateMinute = ParamUtil.getInteger(
					actionRequest, "displayDateMinute");
				int displayOrder = ParamUtil.getInteger(
					actionRequest, "displayOrder");
				int expirationDateMonth = ParamUtil.getInteger(
					actionRequest, "expirationDateMonth");
				int expirationDateDay = ParamUtil.getInteger(
					actionRequest, "expirationDateDay");
				int expirationDateYear = ParamUtil.getInteger(
					actionRequest, "expirationDateYear");
				int expirationDateHour = ParamUtil.getInteger(
					actionRequest, "expirationDateHour");

				int expirationDateAmPm = ParamUtil.getInteger(
					actionRequest, "expirationDateAmPm");

				if (expirationDateAmPm == Calendar.PM) {
					expirationDateHour += 12;
				}

				int expirationDateMinute = ParamUtil.getInteger(
					actionRequest, "expirationDateMinute");
				boolean neverExpire = ParamUtil.getBoolean(
					actionRequest, "neverExpire");

				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(
						CommerceOrderType.class.getName(), actionRequest);

				if (commerceOrderTypeId <= 0) {
					_commerceOrderTypeService.addCommerceOrderType(
						externalReferenceCode, nameMap, descriptionMap, active,
						displayDateMonth, displayDateDay, displayDateYear,
						displayDateHour, displayDateMinute, displayOrder,
						expirationDateMonth, expirationDateDay,
						expirationDateYear, expirationDateHour,
						expirationDateMinute, neverExpire, serviceContext);
				}
				else {
					_commerceOrderTypeService.updateCommerceOrderType(
						externalReferenceCode, commerceOrderTypeId, nameMap,
						descriptionMap, active, displayDateMonth,
						displayDateDay, displayDateYear, displayDateHour,
						displayDateMinute, displayOrder, expirationDateMonth,
						expirationDateDay, expirationDateYear,
						expirationDateHour, expirationDateMinute, neverExpire,
						serviceContext);
				}
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof NoSuchOrderTypeException) {
				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else {
				SessionErrors.add(actionRequest, throwable.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
		}
	}

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private Localization _localization;

}