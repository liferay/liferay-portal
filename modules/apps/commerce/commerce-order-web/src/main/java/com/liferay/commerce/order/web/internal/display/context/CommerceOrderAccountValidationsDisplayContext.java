/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.display.context;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.web.internal.frontend.data.set.filter.AccountEntryValidatorClassNameSelectionFDSFilter;
import com.liferay.commerce.order.web.internal.frontend.data.set.filter.AccountEntryValidatorResultSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Stefano Motta
 */
public class CommerceOrderAccountValidationsDisplayContext {

	public CommerceOrderAccountValidationsDisplayContext(
		AccountEntryValidatorRegistry accountEntryValidatorRegistry,
		CommerceOrder commerceOrder) {

		_accountEntryValidatorRegistry = accountEntryValidatorRegistry;
		_commerceOrder = commerceOrder;
	}

	public String getAccountValidationsURL() throws PortalException {
		String filterString = getFilterString();

		if (Validator.isNull(filterString)) {
			return null;
		}

		return "/o/account/validator-results?filter=" +
			URLCodec.encodeURL(filterString) + "&sort=dateCreated:desc";
	}

	public List<FDSFilter> getFDSFilters() {
		return ListUtil.<FDSFilter>fromArray(
			new AccountEntryValidatorClassNameSelectionFDSFilter(
				_accountEntryValidatorRegistry.getAccountEntryValidators()),
			new AccountEntryValidatorResultSelectionFDSFilter());
	}

	public String getFilterString() throws PortalException {
		if (Validator.isNotNull(_filterString)) {
			return _filterString;
		}

		AccountEntry accountEntry = _commerceOrder.getAccountEntry();

		if (accountEntry == null) {
			return null;
		}

		List<String> validatorClauses = new ArrayList<>();

		for (AccountEntryValidator accountEntryValidator :
				_accountEntryValidatorRegistry.getAccountEntryValidators()) {

			AccountEntryValidatorConfiguration
				accountEntryValidatorConfiguration =
					accountEntryValidator.getAccountEntryValidatorConfiguration(
						accountEntry.getCompanyId());

			if (!accountEntryValidatorConfiguration.enabled()) {
				continue;
			}

			Class<? extends AccountEntryValidator> accountEntryValidatorClass =
				accountEntryValidator.getClass();

			validatorClauses.add(
				StringBundler.concat(
					"((className eq '", accountEntryValidatorClass.getName(),
					"') and (classPK eq '",
					accountEntryValidator.getClassPK(
						accountEntry, _getJSONObject()),
					"'))"));
		}

		if (validatorClauses.isEmpty()) {
			return null;
		}

		_filterString = StringBundler.concat(
			"(", StringUtil.merge(validatorClauses, " or "), ") and ",
			"(r_accountToAccountValidatorResults_accountEntryId eq '",
			accountEntry.getAccountEntryId(), "')");

		return _filterString;
	}

	public boolean isAccountValidationFormVisible() throws PortalException {
		AccountEntry accountEntry = _commerceOrder.getAccountEntry();

		if (accountEntry == null) {
			return false;
		}

		Map<String, AccountEntryValidatorResult>
			accountEntryValidatorResultMap =
				_accountEntryValidatorRegistry.
					getLastAccountEntryValidatorResultsMap(
						accountEntry, _getJSONObject());

		for (AccountEntryValidatorResult accountEntryValidatorResult :
				accountEntryValidatorResultMap.values()) {

			if ((accountEntryValidatorResult == null) ||
				(!Objects.equals(
					AccountEntryValidatorConstants.RESULT_MANUAL,
					accountEntryValidatorResult.getResultStatus()) &&
				 !Objects.equals(
					 AccountEntryValidatorConstants.RESULT_SUCCESS,
					 accountEntryValidatorResult.getResultStatus()))) {

				return true;
			}
		}

		return false;
	}

	private JSONObject _getJSONObject() {
		if (_jsonObject != null) {
			return _jsonObject;
		}

		_jsonObject = JSONUtil.put(
			"billingAddressId", _commerceOrder.getBillingAddressId()
		).put(
			"commerceOrderId", _commerceOrder.getCommerceOrderId()
		).put(
			"shippingAddressId", _commerceOrder.getShippingAddressId()
		);

		return _jsonObject;
	}

	private final AccountEntryValidatorRegistry _accountEntryValidatorRegistry;
	private final CommerceOrder _commerceOrder;
	private String _filterString;
	private JSONObject _jsonObject;

}