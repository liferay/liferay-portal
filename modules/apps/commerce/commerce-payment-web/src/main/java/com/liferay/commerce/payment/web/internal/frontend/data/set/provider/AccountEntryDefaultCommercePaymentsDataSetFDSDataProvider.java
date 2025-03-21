/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.frontend.data.set.provider;

import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelService;
import com.liferay.commerce.payment.web.internal.constants.CommercePaymentMethodGroupRelFDSNames;
import com.liferay.commerce.payment.web.internal.model.PaymentMethod;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelService;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "fds.data.provider.key=" + CommercePaymentMethodGroupRelFDSNames.ACCOUNT_ENTRY_DEFAULT_PAYMENTS,
	service = FDSDataProvider.class
)
public class AccountEntryDefaultCommercePaymentsDataSetFDSDataProvider
	implements FDSDataProvider<PaymentMethod> {

	@Override
	public List<PaymentMethod> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		if (sort == null) {
			sort = new Sort(Field.ENTRY_CLASS_PK, Sort.LONG_TYPE, false);
		}

		long accountEntryId = ParamUtil.getLong(
			httpServletRequest, "accountEntryId");
		Locale locale = _portal.getLocale(httpServletRequest);

		return TransformUtil.transform(
			_commerceChannelService.getEligibleCommerceChannels(
				accountEntryId, fdsKeywords.getKeywords(),
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			commerceChannel -> {
				String active = StringPool.BLANK;

				String paymentMethodName = _language.get(
					httpServletRequest, "use-priority-settings");

				CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
					_commerceChannelAccountEntryRelService.
						fetchCommerceChannelAccountEntryRel(
							accountEntryId,
							commerceChannel.getCommerceChannelId(),
							CommerceChannelAccountEntryRelConstants.
								TYPE_PAYMENT);

				if (commerceChannelAccountEntryRel != null) {
					CommercePaymentMethodGroupRel
						commercePaymentMethodGroupRel =
							_commercePaymentMethodGroupRelService.
								getCommercePaymentMethodGroupRel(
									commerceChannelAccountEntryRel.
										getClassPK());

					if (commercePaymentMethodGroupRel != null) {
						paymentMethodName =
							commercePaymentMethodGroupRel.getName(locale);

						if (commercePaymentMethodGroupRel.isActive()) {
							active = _language.get(locale, "yes");
						}
						else {
							active = _language.get(locale, "no");
						}
					}
				}

				return new PaymentMethod(
					accountEntryId, active, commerceChannel.getName(),
					commerceChannel.getCommerceChannelId(), paymentMethodName);
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		return _commerceChannelService.searchCommerceChannelsCount(
			_portal.getCompanyId(httpServletRequest),
			fdsKeywords.getKeywords());
	}

	@Reference
	private CommerceChannelAccountEntryRelService
		_commerceChannelAccountEntryRelService;

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private CommercePaymentMethodGroupRelService
		_commercePaymentMethodGroupRelService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}