/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.payment.internal.dto.v1_0.converter;

import com.liferay.commerce.constants.CommercePaymentEntryConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.CommercePaymentEntryService;
import com.liferay.headless.commerce.admin.payment.dto.v1_0.Payment;
import com.liferay.headless.commerce.admin.payment.dto.v1_0.Status;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Commerce.Admin.Payment",
		"default=true",
		"dto.class.name=com.liferay.commerce.model.CommercePayment",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class PaymentDTOConverter
	implements DTOConverter<CommercePaymentEntry, Payment> {

	@Override
	public String getContentType() {
		return Payment.class.getSimpleName();
	}

	@Override
	public Payment toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommercePaymentEntry commercePaymentEntry =
			_commercePaymentService.getCommercePaymentEntry(
				GetterUtil.getLong(dtoConverterContext.getId()));

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.getCommerceCurrency(
				commercePaymentEntry.getCompanyId(),
				commercePaymentEntry.getCurrencyCode());

		Locale locale = dtoConverterContext.getLocale();

		ResourceBundle resourceBundle = LanguageResources.getResourceBundle(
			locale);

		return new Payment() {
			{
				setActions(dtoConverterContext::getActions);
				setAmount(commercePaymentEntry::getAmount);
				setAmountFormatted(
					() -> _formatAmount(
						commercePaymentEntry.getAmount(), commerceCurrency,
						locale));
				setAuthor(commercePaymentEntry::getUserName);
				setCallbackURL(commercePaymentEntry::getCallbackURL);
				setCancelURL(commercePaymentEntry::getCancelURL);
				setChannelId(commercePaymentEntry::getCommerceChannelId);
				setComment(commercePaymentEntry::getNote);
				setCreateDate(commercePaymentEntry::getCreateDate);
				setCurrencyCode(commercePaymentEntry::getCurrencyCode);
				setCurrencyExternalReferenceCode(
					commerceCurrency::getExternalReferenceCode);
				setCurrencyId(commerceCurrency::getCommerceCurrencyId);
				setErrorMessages(commercePaymentEntry::getErrorMessages);
				setExternalReferenceCode(
					commercePaymentEntry::getExternalReferenceCode);
				setId(commercePaymentEntry::getCommercePaymentEntryId);
				setLanguageId(commercePaymentEntry::getLanguageId);
				setPayload(commercePaymentEntry::getPayload);
				setPaymentIntegrationKey(
					commercePaymentEntry::getPaymentIntegrationKey);
				setPaymentIntegrationType(
					commercePaymentEntry::getPaymentIntegrationType);
				setPaymentStatus(commercePaymentEntry::getPaymentStatus);
				setPaymentStatusStatus(
					() -> _toPaymentStatusStatus(
						commercePaymentEntry.getPaymentStatus(),
						CommercePaymentEntryConstants.getPaymentStatusLabel(
							commercePaymentEntry.getPaymentStatus()),
						_language.get(
							resourceBundle,
							CommercePaymentEntryConstants.getPaymentStatusLabel(
								commercePaymentEntry.getPaymentStatus()))));
				setReasonKey(commercePaymentEntry::getReasonKey);
				setReasonName(
					() -> LanguageUtils.getLanguageIdMap(
						commercePaymentEntry.getReasonNameMap()));
				setRedirectURL(commercePaymentEntry::getRedirectURL);
				setRelatedItemId(commercePaymentEntry::getClassPK);
				setRelatedItemName(commercePaymentEntry::getClassName);
				setRelatedItemNameLabel(
					() -> _language.get(
						resourceBundle,
						"model.resource." +
							commercePaymentEntry.getClassName()));
				setTransactionCode(commercePaymentEntry::getTransactionCode);
				setType(commercePaymentEntry::getType);
				setTypeLabel(
					() -> _language.get(
						resourceBundle,
						CommercePaymentEntryConstants.getTypeLabel(
							commercePaymentEntry.getType())));
			}
		};
	}

	private String _formatAmount(
			BigDecimal amount, CommerceCurrency commerceCurrency, Locale locale)
		throws Exception {

		if (amount == null) {
			amount = BigDecimal.ZERO;
		}

		return _commercePriceFormatter.format(
			commerceCurrency, true, locale, amount);
	}

	private Status _toPaymentStatusStatus(
		int paymentStatus, String commercePaymentEntryPaymentStatusLabel,
		String commercePaymentEntryPaymentStatusLabelI18n) {

		return new Status() {
			{
				setCode(() -> paymentStatus);
				setLabel(() -> commercePaymentEntryPaymentStatusLabel);
				setLabel_i18n(() -> commercePaymentEntryPaymentStatusLabelI18n);
			}
		};
	}

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommercePaymentEntryService _commercePaymentService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private Language _language;

}