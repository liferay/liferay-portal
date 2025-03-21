/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactoryUtil;
import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.web.internal.model.Refund;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.REFUNDS,
	service = FDSDataProvider.class
)
public class CommerceRefundFDSDataProvider implements FDSDataProvider<Refund> {

	@Override
	public List<Refund> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		List<Refund> refunds = new ArrayList<>();

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		for (CommercePaymentEntry commercePaymentEntry :
				_commercePaymentEntryLocalService.
					getRefundCommercePaymentEntries(
						commerceOrder.getCompanyId(),
						_classNameLocalService.getClassNameId(
							CommerceOrder.class),
						commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
						QueryUtil.ALL_POS)) {

			refunds.add(
				new Refund(
					_formatCommercePaymentEntryAmount(
						commerceOrder.getCommerceCurrency(),
						commercePaymentEntry.getAmount(), themeDisplay),
					dateTimeFormat.format(commercePaymentEntry.getCreateDate()),
					commercePaymentEntry.getExternalReferenceCode(),
					commercePaymentEntry.getCommercePaymentEntryId(),
					commercePaymentEntry.getClassNameId(),
					commercePaymentEntry.getClassName(),
					new LabelField(
						CommerceOrderPaymentConstants.getOrderPaymentLabelStyle(
							commercePaymentEntry.getPaymentStatus()),
						_language.get(
							httpServletRequest,
							CommerceOrderPaymentConstants.
								getOrderPaymentStatusLabel(
									commercePaymentEntry.
										getPaymentStatus())))));
		}

		return refunds;
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		return _commercePaymentEntryLocalService.
			getRefundCommercePaymentEntriesCount(
				commerceOrder.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				commerceOrder.getCommerceOrderId());
	}

	private String _formatCommercePaymentEntryAmount(
			CommerceCurrency commerceCurrency, BigDecimal amount,
			ThemeDisplay themeDisplay)
		throws PortalException {

		CommerceMoney commerceMoney = CommerceMoneyFactoryUtil.create(
			commerceCurrency, amount);

		return commerceMoney.format(themeDisplay.getLocale());
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

	@Reference
	private Language _language;

}