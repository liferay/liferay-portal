/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.exception.CommerceDiscountAmountException;
import com.liferay.commerce.discount.exception.CommerceDiscountCouponCodeException;
import com.liferay.commerce.discount.exception.CommerceDiscountMaxPriceValueException;
import com.liferay.commerce.discount.exception.CommerceDiscountMinPriceValueException;
import com.liferay.commerce.discount.exception.CommerceDiscountRuleTypeSettingsException;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_DISCOUNT,
		"mvc.command.name=/commerce_discount/edit_commerce_discount"
	},
	service = MVCActionCommand.class
)
public class EditCommerceDiscountMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateCommerceDiscount(actionRequest);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof CommerceDiscountCouponCodeException ||
				throwable instanceof
					CommerceDiscountRuleTypeSettingsException ||
				throwable instanceof NoSuchDiscountException) {

				SessionErrors.add(
					actionRequest, throwable.getClass(), throwable);

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (throwable instanceof CommerceDiscountAmountException ||
					 throwable instanceof
						 CommerceDiscountMaxPriceValueException ||
					 throwable instanceof
						 CommerceDiscountMinPriceValueException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, throwable.getClass());

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

	private BigDecimal[] _getDiscountLevels(String level, BigDecimal amount) {
		BigDecimal[] discountLevels = {
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
		};

		if (Objects.equals(level, CommerceDiscountConstants.LEVEL_L1)) {
			discountLevels[0] = amount;
		}

		if (Objects.equals(level, CommerceDiscountConstants.LEVEL_L2)) {
			discountLevels[1] = amount;
		}

		if (Objects.equals(level, CommerceDiscountConstants.LEVEL_L3)) {
			discountLevels[2] = amount;
		}

		if (Objects.equals(level, CommerceDiscountConstants.LEVEL_L4)) {
			discountLevels[3] = amount;
		}

		return discountLevels;
	}

	private String _getLimitationType(
		int limitationTimes, int limitationTimesPerAccount) {

		if ((limitationTimes > 0) && (limitationTimesPerAccount > 0)) {
			return CommerceDiscountConstants.
				LIMITATION_TYPE_LIMITED_FOR_ACCOUNTS_AND_TOTAL;
		}

		if (limitationTimes > 0) {
			return CommerceDiscountConstants.LIMITATION_TYPE_LIMITED;
		}

		if (limitationTimesPerAccount > 0) {
			return CommerceDiscountConstants.
				LIMITATION_TYPE_LIMITED_FOR_ACCOUNTS;
		}

		return CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED;
	}

	private CommerceDiscount _updateCommerceDiscount(
			ActionRequest actionRequest)
		throws Exception {

		long commerceDiscountId = ParamUtil.getLong(
			actionRequest, "commerceDiscountId");

		String title = ParamUtil.getString(actionRequest, "title");

		String target = ParamUtil.getString(actionRequest, "target");

		boolean useCouponCode = ParamUtil.getBoolean(
			actionRequest, "useCouponCode");

		String couponCode = ParamUtil.getString(actionRequest, "couponCode");

		if (!useCouponCode) {
			couponCode = null;
		}

		boolean usePercentage = ParamUtil.getBoolean(
			actionRequest, "usePercentage");

		String level = ParamUtil.getString(actionRequest, "level");

		BigDecimal[] discountLevels = _getDiscountLevels(
			level,
			_commercePriceFormatter.parse(
				actionRequest, false, CommerceDiscount.class.getName(),
				"amount"));

		int limitationTimes = ParamUtil.getInteger(
			actionRequest, "limitationTimes");
		int limitationTimesPerAccount = ParamUtil.getInteger(
			actionRequest, "limitationTimesPerAccount");

		int displayDateHour = ParamUtil.getInteger(
			actionRequest, "displayDateHour");

		int displayDateAmPm = ParamUtil.getInteger(
			actionRequest, "displayDateAmPm");

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		int expirationDateHour = ParamUtil.getInteger(
			actionRequest, "expirationDateHour");

		int expirationDateAmPm = ParamUtil.getInteger(
			actionRequest, "expirationDateAmPm");

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		return _commerceDiscountService.addOrUpdateCommerceDiscount(
			ParamUtil.getString(actionRequest, "externalReferenceCode"),
			commerceDiscountId, title, target, useCouponCode, couponCode,
			usePercentage,
			_commercePriceFormatter.parse(
				actionRequest, false, CommerceDiscount.class.getName(),
				"maximumDiscountAmount"),
			level, discountLevels[0], discountLevels[1], discountLevels[2],
			discountLevels[3],
			_getLimitationType(limitationTimes, limitationTimesPerAccount),
			limitationTimes, limitationTimesPerAccount,
			ParamUtil.getBoolean(actionRequest, "rulesConjunction"),
			ParamUtil.getBoolean(actionRequest, "active"),
			ParamUtil.getInteger(actionRequest, "displayDateMonth"),
			ParamUtil.getInteger(actionRequest, "displayDateDay"),
			ParamUtil.getInteger(actionRequest, "displayDateYear"),
			displayDateHour,
			ParamUtil.getInteger(actionRequest, "displayDateMinute"),
			ParamUtil.getInteger(actionRequest, "expirationDateMonth"),
			ParamUtil.getInteger(actionRequest, "expirationDateDay"),
			ParamUtil.getInteger(actionRequest, "expirationDateYear"),
			expirationDateHour,
			ParamUtil.getInteger(actionRequest, "expirationDateMinute"),
			ParamUtil.getBoolean(actionRequest, "neverExpire"),
			ServiceContextFactory.getInstance(
				CommerceDiscount.class.getName(), actionRequest));
	}

	@Reference
	private CommerceDiscountService _commerceDiscountService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

}