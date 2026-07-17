/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.internal.validator;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.CommerceOrderValidator;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.order.rule.entry.type.COREntryType;
import com.liferay.commerce.order.rule.entry.type.COREntryTypeRegistry;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.COREntryLocalService;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"commerce.order.validator.key=" + CORCommerceOrderValidator.KEY,
		"commerce.order.validator.priority:Integer=50"
	},
	service = CommerceOrderValidator.class
)
public class CORCommerceOrderValidator implements CommerceOrderValidator {

	public static final String KEY = "order-rules";

	@Override
	public String getKey() {
		return CORCommerceOrderValidator.KEY;
	}

	@Override
	public CommerceOrderValidatorResult validate(
			Locale locale, CommerceOrder commerceOrder)
		throws PortalException {

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		List<COREntry> corEntries =
			_corEntryLocalService.
				getAccountEntryAndCommerceChannelAndCommerceOrderTypeCOREntries(
					commerceOrder.getCompanyId(),
					accountEntry.getAccountEntryId(),
					commerceChannel.getCommerceChannelId(),
					commerceOrder.getCommerceOrderTypeId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries =
			_corEntryLocalService.getAccountEntryAndCommerceOrderTypeCOREntries(
				commerceOrder.getCompanyId(), accountEntry.getAccountEntryId(),
				commerceOrder.getCommerceOrderTypeId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries =
			_corEntryLocalService.getAccountEntryAndCommerceChannelCOREntries(
				commerceOrder.getCompanyId(), accountEntry.getAccountEntryId(),
				commerceChannel.getCommerceChannelId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries = _corEntryLocalService.getAccountEntryCOREntries(
			commerceOrder.getCompanyId(), accountEntry.getAccountEntryId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		long[] commerceAccountGroupIds =
			_accountGroupLocalService.getAccountGroupIds(
				accountEntry.getAccountEntryId());

		corEntries =
			_corEntryLocalService.
				getAccountGroupsAndCommerceChannelAndCommerceOrderTypeCOREntries(
					commerceOrder.getCompanyId(), commerceAccountGroupIds,
					commerceChannel.getCommerceChannelId(),
					commerceOrder.getCommerceOrderTypeId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries =
			_corEntryLocalService.
				getAccountGroupsAndCommerceOrderTypeCOREntries(
					commerceOrder.getCompanyId(), commerceAccountGroupIds,
					commerceOrder.getCommerceOrderTypeId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries =
			_corEntryLocalService.getAccountGroupsAndCommerceChannelCOREntries(
				commerceOrder.getCompanyId(), commerceAccountGroupIds,
				commerceChannel.getCommerceChannelId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries = _corEntryLocalService.getAccountGroupsCOREntries(
			commerceOrder.getCompanyId(), commerceAccountGroupIds);

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries =
			_corEntryLocalService.
				getCommerceChannelAndCommerceOrderTypeCOREntries(
					commerceOrder.getCompanyId(),
					commerceChannel.getCommerceChannelId(),
					commerceOrder.getCommerceOrderTypeId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries = _corEntryLocalService.getCommerceOrderTypeCOREntries(
			commerceOrder.getCompanyId(),
			commerceOrder.getCommerceOrderTypeId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries = _corEntryLocalService.getCommerceChannelCOREntries(
			commerceOrder.getCompanyId(),
			commerceChannel.getCommerceChannelId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		corEntries = _corEntryLocalService.getUnqualifiedCOREntries(
			commerceOrder.getCompanyId());

		if (!corEntries.isEmpty()) {
			String errorMessage = _validate(commerceOrder, corEntries, locale);

			if (Validator.isBlank(errorMessage)) {
				return new CommerceOrderValidatorResult(true);
			}

			return new CommerceOrderValidatorResult(false, errorMessage);
		}

		return new CommerceOrderValidatorResult(true);
	}

	@Override
	public CommerceOrderValidatorResult validate(
			Locale locale, CommerceOrder commerceOrder, CPInstance cpInstance,
			String json, BigDecimal quantity, boolean child)
		throws PortalException {

		return new CommerceOrderValidatorResult(true);
	}

	@Override
	public CommerceOrderValidatorResult validate(
			Locale locale, CommerceOrderItem commerceOrderItem)
		throws PortalException {

		return new CommerceOrderValidatorResult(true);
	}

	private String _validate(
			CommerceOrder commerceOrder, List<COREntry> corEntries,
			Locale locale)
		throws PortalException {

		Set<String> validatedRuleType = new HashSet<>();

		for (COREntry corEntry : corEntries) {
			String type = corEntry.getType();

			if (validatedRuleType.add(type)) {
				COREntryType corEntryType =
					_corEntryTypeRegistry.getCOREntryType(type);

				if (!corEntryType.evaluate(corEntry, commerceOrder)) {
					return corEntryType.getErrorMessage(
						corEntry, commerceOrder, locale);
				}
			}
		}

		return null;
	}

	@Reference
	private AccountGroupLocalService _accountGroupLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private COREntryLocalService _corEntryLocalService;

	@Reference
	private COREntryTypeRegistry _corEntryTypeRegistry;

}