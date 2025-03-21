/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.web.internal.portlet.action;

import com.liferay.commerce.order.rule.constants.COREntryConstants;
import com.liferay.commerce.order.rule.constants.COREntryPortletKeys;
import com.liferay.commerce.order.rule.exception.NoSuchCOREntryException;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Calendar;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + COREntryPortletKeys.COR_ENTRY,
		"mvc.command.name=/cor_entry/edit_cor_entry"
	},
	service = MVCActionCommand.class
)
public class EditCOREntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			long corEntryId = ParamUtil.getLong(actionRequest, "corEntryId");

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				boolean active = ParamUtil.getBoolean(actionRequest, "active");
				String description = ParamUtil.getString(
					actionRequest, "description");
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
				String name = ParamUtil.getString(actionRequest, "name");
				int priority = ParamUtil.getInteger(actionRequest, "priority");

				ServiceContext serviceContext =
					ServiceContextFactory.getInstance(
						COREntry.class.getName(), actionRequest);

				if (corEntryId <= 0) {
					_corEntryService.addCOREntry(
						ParamUtil.getString(
							actionRequest, "externalReferenceCode"),
						active, description, displayDateMonth, displayDateDay,
						displayDateYear, displayDateHour, displayDateMinute,
						expirationDateMonth, expirationDateDay,
						expirationDateYear, expirationDateHour,
						expirationDateMinute, neverExpire, name, priority,
						ParamUtil.getString(actionRequest, "type"),
						_getTypeSettings(actionRequest), serviceContext);
				}
				else {
					_corEntryService.updateCOREntry(
						corEntryId, active, description, displayDateMonth,
						displayDateDay, displayDateYear, displayDateHour,
						displayDateMinute, expirationDateMonth,
						expirationDateDay, expirationDateYear,
						expirationDateHour, expirationDateMinute, neverExpire,
						name, priority, _getTypeSettings(actionRequest),
						serviceContext);
				}
			}
			else if (cmd.equals("deleteProduct")) {
				COREntry corEntry = _corEntryService.getCOREntry(corEntryId);

				_corEntryService.updateCOREntryTypeSettings(
					corEntry.getCOREntryId(),
					_getTypeSettings(actionRequest, corEntry));
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof NoSuchCOREntryException) {
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

	private String _getTypeSettings(ActionRequest actionRequest) {
		UnicodeProperties typeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "type--settings--");

		String quantity = typeSettingsUnicodeProperties.getProperty(
			COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_QUANTITY);

		if (Validator.isNotNull(quantity)) {
			try {
				quantity = String.valueOf(Double.valueOf(quantity));
			}
			catch (NumberFormatException numberFormatException) {
				if (_log.isDebugEnabled()) {
					_log.debug(numberFormatException);
				}

				quantity = "0";
			}

			typeSettingsUnicodeProperties.setProperty(
				COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_QUANTITY,
				quantity);
		}

		return typeSettingsUnicodeProperties.toString();
	}

	private String _getTypeSettings(
		ActionRequest actionRequest, COREntry corEntry) {

		String cProductId = ParamUtil.getString(actionRequest, "cProductId");

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				corEntry.getTypeSettings()
			).build();

		List<String> cProductIds = StringUtil.split(
			typeSettingsUnicodeProperties.getProperty(
				COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS));

		cProductIds.remove(cProductId);

		return UnicodePropertiesBuilder.create(
			true
		).setProperty(
			COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_IDS,
			StringUtil.merge(cProductIds, StringPool.COMMA)
		).setProperty(
			COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_QUANTITY,
			typeSettingsUnicodeProperties.getProperty(
				COREntryConstants.TYPE_PRODUCTS_LIMIT_FIELD_PRODUCT_QUANTITY)
		).buildString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditCOREntryMVCActionCommand.class);

	@Reference
	private COREntryService _corEntryService;

}