/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.subscription.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderPaymentLocalService;
import com.liferay.commerce.service.CommerceSubscriptionEntryLocalService;
import com.liferay.commerce.subscription.web.internal.constants.CommerceSubscriptionFDSNames;
import com.liferay.commerce.subscription.web.internal.model.Payment;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.Format;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "fds.data.provider.key=" + CommerceSubscriptionFDSNames.SUBSCRIPTION_PAYMENTS,
	service = FDSDataProvider.class
)
public class CommerceSubscriptionPaymentsFDSDataProvider
	implements FDSDataProvider<Payment> {

	@Override
	public List<Payment> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			DateFormat.MEDIUM, DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		long commerceSubscriptionEntryId = ParamUtil.getLong(
			httpServletRequest, "commerceSubscriptionEntryId");

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_commerceSubscriptionEntryLocalService.getCommerceSubscriptionEntry(
				commerceSubscriptionEntryId);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(
				commerceSubscriptionEntry.getCommerceOrderItemId());

		return TransformUtil.transform(
			_commerceOrderPaymentLocalService.getCommerceOrderPayments(
				commerceOrderItem.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null),
			commerceOrderPayment -> {
				CommerceOrder commerceOrder =
					commerceOrderItem.getCommerceOrder();

				CommerceCurrency commerceCurrency =
					commerceOrder.getCommerceCurrency();

				BigDecimal finalPrice = commerceOrderItem.getFinalPrice();

				return new Payment(
					new LabelField(
						CommerceOrderPaymentConstants.getOrderPaymentLabelStyle(
							commerceOrderPayment.getStatus()),
						_language.get(
							httpServletRequest,
							CommerceOrderPaymentConstants.
								getOrderPaymentStatusLabel(
									commerceOrderPayment.getStatus()))),
					dateTimeFormat.format(commerceOrderPayment.getCreateDate()),
					commerceOrderPayment.getCommerceOrderPaymentId(),
					StringBundler.concat(
						commerceCurrency.round(finalPrice), CharPool.SPACE,
						commerceCurrency.getCode()));
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceSubscriptionEntryId = ParamUtil.getLong(
			httpServletRequest, "commerceSubscriptionEntryId");

		CommerceSubscriptionEntry commerceSubscriptionEntry =
			_commerceSubscriptionEntryLocalService.getCommerceSubscriptionEntry(
				commerceSubscriptionEntryId);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(
				commerceSubscriptionEntry.getCommerceOrderItemId());

		return _commerceOrderPaymentLocalService.getCommerceOrderPaymentsCount(
			commerceOrderItem.getCommerceOrderId());
	}

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderPaymentLocalService _commerceOrderPaymentLocalService;

	@Reference
	private CommerceSubscriptionEntryLocalService
		_commerceSubscriptionEntryLocalService;

	@Reference
	private Language _language;

}