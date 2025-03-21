/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.web.internal.portlet.action;

import com.liferay.commerce.term.constants.CommerceTermEntryPortletKeys;
import com.liferay.commerce.term.exception.CommerceTermEntryNameException;
import com.liferay.commerce.term.exception.CommerceTermEntryPriorityException;
import com.liferay.commerce.term.exception.CommerceTermEntryTypeException;
import com.liferay.commerce.term.exception.NoSuchTermEntryException;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceTermEntryPortletKeys.COMMERCE_TERM_ENTRY,
		"mvc.command.name=/commerce_term_entry/edit_commerce_term_entry"
	},
	service = MVCActionCommand.class
)
public class EditCommerceTermEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				long commerceTermEntryId = ParamUtil.getLong(
					actionRequest, "commerceTermEntryId");

				boolean active = ParamUtil.getBoolean(actionRequest, "active");
				Map<Locale, String> descriptionMap =
					_localization.getLocalizationMap(
						actionRequest, "descriptionMapAsXML");
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
				Map<Locale, String> labelMap = _localization.getLocalizationMap(
					actionRequest, "labelMapAsXML");
				String name = ParamUtil.getString(actionRequest, "name");
				double priority = ParamUtil.getDouble(
					actionRequest, "priority");

				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(
						CommerceTermEntry.class.getName(), actionRequest);

				if (commerceTermEntryId <= 0) {
					_commerceTermEntryService.addCommerceTermEntry(
						ParamUtil.getString(
							actionRequest, "externalReferenceCode"),
						active, descriptionMap, displayDateMonth,
						displayDateDay, displayDateYear, displayDateHour,
						displayDateMinute, expirationDateMonth,
						expirationDateDay, expirationDateYear,
						expirationDateHour, expirationDateMinute, neverExpire,
						labelMap, name, priority,
						ParamUtil.getString(actionRequest, "type"),
						_getTypeSettings(actionRequest), serviceContext);
				}
				else {
					_commerceTermEntryService.updateCommerceTermEntry(
						commerceTermEntryId, active, descriptionMap,
						displayDateMonth, displayDateDay, displayDateYear,
						displayDateHour, displayDateMinute, expirationDateMonth,
						expirationDateDay, expirationDateYear,
						expirationDateHour, expirationDateMinute, neverExpire,
						labelMap, name, priority,
						_getTypeSettings(actionRequest), serviceContext);
				}
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof CommerceTermEntryNameException ||
				throwable instanceof CommerceTermEntryPriorityException ||
				throwable instanceof CommerceTermEntryTypeException ||
				throwable instanceof NoSuchTermEntryException) {

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

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

	private String _getTypeSettings(ActionRequest actionRequest) {
		UnicodeProperties typeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "type--settings--");

		return typeSettingsUnicodeProperties.toString();
	}

	@Reference
	private CommerceTermEntryService _commerceTermEntryService;

	@Reference
	private Localization _localization;

}