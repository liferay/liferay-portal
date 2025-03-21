/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.portlet.action;

import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.price.list.exception.CommercePriceEntryPriceException;
import com.liferay.commerce.price.list.exception.CommercePriceListMaxPriceValueException;
import com.liferay.commerce.price.list.exception.CommercePriceListMinPriceValueException;
import com.liferay.commerce.price.list.exception.DuplicateCommercePriceEntryException;
import com.liferay.commerce.price.list.exception.DuplicateCommercePriceEntryExternalReferenceCodeException;
import com.liferay.commerce.price.list.exception.NoSuchPriceEntryException;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryService;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.math.BigDecimal;

import java.util.Calendar;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PRICE_LIST,
		"jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_PROMOTION,
		"mvc.command.name=/commerce_price_list/edit_commerce_price_entry"
	},
	service = MVCActionCommand.class
)
public class EditCommercePriceEntryMVCActionCommand
	extends BaseMVCActionCommand {

	protected void addCommercePriceEntries(ActionRequest actionRequest)
		throws Exception {

		long[] addCPInstanceIds = null;

		long commercePriceListId = ParamUtil.getLong(
			actionRequest, "commercePriceListId");

		long cpInstanceId = ParamUtil.getLong(actionRequest, "cpInstanceId");

		if (cpInstanceId > 0) {
			addCPInstanceIds = new long[] {cpInstanceId};
		}
		else {
			addCPInstanceIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "cpInstanceIds"), 0L);
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			CommercePriceEntry.class.getName(), actionRequest);

		for (long addCPInstanceId : addCPInstanceIds) {
			CPInstance cpInstance = _cpInstanceService.getCPInstance(
				addCPInstanceId);

			_commercePriceEntryService.addCommercePriceEntry(
				null, addCPInstanceId, commercePriceListId,
				cpInstance.getPrice(), false, cpInstance.getPromoPrice(), null,
				serviceContext);
		}
	}

	protected void deleteCommercePriceEntries(
			long commercePriceEntryId, ActionRequest actionRequest)
		throws Exception {

		long[] deleteCommercePriceEntryIds = null;

		if (commercePriceEntryId > 0) {
			deleteCommercePriceEntryIds = new long[] {commercePriceEntryId};
		}
		else {
			deleteCommercePriceEntryIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "deleteCommercePriceEntryIds"),
				0L);
		}

		for (long deleteCommercePriceEntryId : deleteCommercePriceEntryIds) {
			_commercePriceEntryService.deleteCommercePriceEntry(
				deleteCommercePriceEntryId);
		}
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		long commercePriceEntryId = ParamUtil.getLong(
			actionRequest, "commercePriceEntryId");

		try {
			if (cmd.equals(Constants.ADD) ||
				cmd.equals(Constants.ADD_MULTIPLE)) {

				addCommercePriceEntries(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				deleteCommercePriceEntries(commercePriceEntryId, actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				updateCommercePriceEntry(commercePriceEntryId, actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof CommercePriceEntryPriceException ||
				exception instanceof CommercePriceListMaxPriceValueException ||
				exception instanceof CommercePriceListMinPriceValueException ||
				exception instanceof DuplicateCommercePriceEntryException) {

				hideDefaultErrorMessage(actionRequest);
				hideDefaultSuccessMessage(actionRequest);

				SessionErrors.add(actionRequest, exception.getClass());

				String redirect = ParamUtil.getString(
					actionRequest, "redirect");

				sendRedirect(actionRequest, actionResponse, redirect);
			}
			else if (exception instanceof
						DuplicateCommercePriceEntryExternalReferenceCodeException ||
					 exception instanceof NoSuchCPInstanceException ||
					 exception instanceof NoSuchPriceEntryException ||
					 exception instanceof NoSuchPriceListException ||
					 exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	protected CommercePriceEntry updateCommercePriceEntry(
			long commercePriceEntryId, ActionRequest actionRequest)
		throws Exception {

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryService.getCommercePriceEntry(
				commercePriceEntryId);

		boolean bulkPricing = ParamUtil.getBoolean(
			actionRequest, "bulkPricing");
		boolean overrideDiscount = ParamUtil.getBoolean(
			actionRequest, "overrideDiscount");
		BigDecimal discountLevel1 = _commercePriceFormatter.parse(
			actionRequest, false, CommercePriceEntry.class.getName(),
			"discountLevel1");
		BigDecimal discountLevel2 = _commercePriceFormatter.parse(
			actionRequest, false, CommercePriceEntry.class.getName(),
			"discountLevel2");
		BigDecimal discountLevel3 = _commercePriceFormatter.parse(
			actionRequest, false, CommercePriceEntry.class.getName(),
			"discountLevel3");
		BigDecimal discountLevel4 = _commercePriceFormatter.parse(
			actionRequest, false, CommercePriceEntry.class.getName(),
			"discountLevel4");
		int displayDateMonth = ParamUtil.getInteger(
			actionRequest, "displayDateMonth");
		int displayDateDay = ParamUtil.getInteger(
			actionRequest, "displayDateDay");
		int displayDateYear = ParamUtil.getInteger(
			actionRequest, "displayDateYear");
		int displayDateHour = ParamUtil.getInteger(
			actionRequest, "displayDateHour");
		int displayDateMinute = ParamUtil.getInteger(
			actionRequest, "displayDateMinute");
		int displayDateAmPm = ParamUtil.getInteger(
			actionRequest, "displayDateAmPm");

		if (displayDateAmPm == Calendar.PM) {
			displayDateHour += 12;
		}

		int expirationDateMonth = ParamUtil.getInteger(
			actionRequest, "expirationDateMonth");
		int expirationDateDay = ParamUtil.getInteger(
			actionRequest, "expirationDateDay");
		int expirationDateYear = ParamUtil.getInteger(
			actionRequest, "expirationDateYear");
		int expirationDateHour = ParamUtil.getInteger(
			actionRequest, "expirationDateHour");
		int expirationDateMinute = ParamUtil.getInteger(
			actionRequest, "expirationDateMinute");
		int expirationDateAmPm = ParamUtil.getInteger(
			actionRequest, "expirationDateAmPm");

		if (expirationDateAmPm == Calendar.PM) {
			expirationDateHour += 12;
		}

		boolean neverExpire = ParamUtil.getBoolean(
			actionRequest, "neverExpire");

		BigDecimal price = _commercePriceFormatter.parse(
			actionRequest, false, CommercePriceEntry.class.getName(), "price");
		boolean priceOnApplication = ParamUtil.getBoolean(
			actionRequest, "priceOnApplication");

		if (priceOnApplication) {
			bulkPricing = commercePriceEntry.isBulkPricing();
			overrideDiscount = !commercePriceEntry.isDiscountDiscovery();
			discountLevel1 = commercePriceEntry.getDiscountLevel1();
			discountLevel2 = commercePriceEntry.getDiscountLevel2();
			discountLevel3 = commercePriceEntry.getDiscountLevel3();
			discountLevel4 = commercePriceEntry.getDiscountLevel4();
			price = commercePriceEntry.getPrice();
		}

		return _commercePriceEntryService.updateCommercePriceEntry(
			commercePriceEntryId, bulkPricing, !overrideDiscount,
			discountLevel1, discountLevel2, discountLevel3, discountLevel4,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, price, priceOnApplication,
			commercePriceEntry.getUnitOfMeasureKey(),
			ServiceContextFactory.getInstance(
				CommercePriceEntry.class.getName(), actionRequest));
	}

	@Reference
	private CommercePriceEntryService _commercePriceEntryService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CPInstanceService _cpInstanceService;

}