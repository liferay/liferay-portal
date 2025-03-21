/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.model.CommerceMoneyFactoryUtil;
import com.liferay.commerce.frontend.model.LabelField;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFDSNames;
import com.liferay.commerce.order.content.web.internal.model.PaymentEntry;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryLocalService;
import com.liferay.commerce.payment.util.comparator.CommercePaymentEntryComparator;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
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

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "fds.data.provider.key=" + CommerceOrderFDSNames.REFUND_ITEMS,
	service = FDSDataProvider.class
)
public class CommerceRefundItemFDSDataProvider
	implements FDSDataProvider<PaymentEntry> {

	@Override
	public List<PaymentEntry> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		List<CommercePaymentEntry> commerceOrderCommercePaymentEntries =
			_commercePaymentEntryLocalService.getCommercePaymentEntries(
				commerceOrder.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				commerceOrder.getCommerceOrderId(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, new CommercePaymentEntryComparator(true));

		long commerceOrderCommercePaymentEntryId = 0;

		for (CommercePaymentEntry commercePaymentEntry :
				commerceOrderCommercePaymentEntries) {

			if (commercePaymentEntry.getPaymentStatus() ==
					CommercePaymentEntryConstants.STATUS_COMPLETED) {

				commerceOrderCommercePaymentEntryId =
					commercePaymentEntry.getCommercePaymentEntryId();

				break;
			}
		}

		return TransformUtil.transform(
			_commercePaymentEntryLocalService.getCommercePaymentEntries(
				commerceOrder.getCompanyId(),
				_classNameLocalService.getClassNameId(
					CommercePaymentEntry.class),
				commerceOrderCommercePaymentEntryId,
				CommercePaymentEntryConstants.TYPE_REFUND, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, new CommercePaymentEntryComparator(true)),
			commercePaymentEntry -> _toPaymentEntry(
				commercePaymentEntry, commerceOrder.getCommerceCurrency(),
				themeDisplay));
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceOrderId = ParamUtil.getLong(
			httpServletRequest, "commerceOrderId");

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrderId);

		List<CommercePaymentEntry> commerceOrderCommercePaymentEntries =
			_commercePaymentEntryLocalService.getCommercePaymentEntries(
				commerceOrder.getCompanyId(),
				_classNameLocalService.getClassNameId(CommerceOrder.class),
				commerceOrder.getCommerceOrderId(),
				CommercePaymentEntryConstants.TYPE_PAYMENT, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, new CommercePaymentEntryComparator(true));

		long commerceOrderCommercePaymentEntryId = 0;

		for (CommercePaymentEntry commercePaymentEntry :
				commerceOrderCommercePaymentEntries) {

			if (commercePaymentEntry.getPaymentStatus() ==
					CommercePaymentEntryConstants.STATUS_COMPLETED) {

				commerceOrderCommercePaymentEntryId =
					commercePaymentEntry.getCommercePaymentEntryId();

				break;
			}
		}

		return _commercePaymentEntryLocalService.getCommercePaymentEntriesCount(
			commerceOrder.getCompanyId(),
			_classNameLocalService.getClassNameId(CommercePaymentEntry.class),
			commerceOrderCommercePaymentEntryId,
			CommercePaymentEntryConstants.TYPE_REFUND);
	}

	private String _formatCommercePaymentEntryAmount(
			CommerceCurrency commerceCurrency, BigDecimal amount,
			ThemeDisplay themeDisplay)
		throws PortalException {

		CommerceMoney commerceMoney = CommerceMoneyFactoryUtil.create(
			commerceCurrency, amount);

		return commerceMoney.format(themeDisplay.getLocale());
	}

	private String _formatCommercePaymentEntryCreateDate(
		ThemeDisplay themeDisplay, Date commercePaymentEntryCreateDate) {

		Format dateFormat = FastDateFormatFactoryUtil.getDate(
			DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());
		Format timeFormat = FastDateFormatFactoryUtil.getTime(
			DateFormat.MEDIUM, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());

		return StringBundler.concat(
			dateFormat.format(commercePaymentEntryCreateDate), StringPool.SPACE,
			timeFormat.format(commercePaymentEntryCreateDate));
	}

	private PaymentEntry _toPaymentEntry(
			CommercePaymentEntry commercePaymentEntry,
			CommerceCurrency commerceCurrency, ThemeDisplay themeDisplay)
		throws PortalException {

		return new PaymentEntry(
			_formatCommercePaymentEntryAmount(
				commerceCurrency, commercePaymentEntry.getAmount(),
				themeDisplay),
			_formatCommercePaymentEntryCreateDate(
				themeDisplay, commercePaymentEntry.getCreateDate()),
			new LabelField(
				CommercePaymentEntryConstants.getPaymentLabelStyle(
					commercePaymentEntry.getPaymentStatus()),
				_language.get(
					themeDisplay.getLocale(),
					CommercePaymentEntryConstants.getPaymentStatusLabel(
						commercePaymentEntry.getPaymentStatus()))));
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