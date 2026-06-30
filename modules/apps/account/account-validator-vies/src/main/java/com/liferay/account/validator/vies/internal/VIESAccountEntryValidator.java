/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.validator.vies.internal;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.account.constants.AccountConstants;
import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.validator.AccountEntryValidator;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.account.validator.vies.configuration.VIESAccountEntryValidatorConfiguration;
import com.liferay.account.validator.vies.internal.client.VIESClient;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = {
		"account.entry.validator.key=" + VIESAccountEntryValidator.KEY,
		"account.entry.validator.priority:Integer=10"
	},
	service = AccountEntryValidator.class
)
public class VIESAccountEntryValidator implements AccountEntryValidator {

	public static final String KEY = "vies";

	@Override
	public AccountEntryValidatorConfiguration
			getAccountEntryValidatorConfiguration(long companyId)
		throws PortalException {

		return _configurationProvider.getCompanyConfiguration(
			VIESAccountEntryValidatorConfiguration.class, companyId);
	}

	@Override
	public String getClassPK(AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException {

		VIESAccountEntryValidatorConfiguration
			viesAccountEntryValidatorConfiguration =
				_getVIESAccountEntryValidatorConfiguration(
					accountEntry, jsonObject);

		if (viesAccountEntryValidatorConfiguration == null) {
			return null;
		}

		long billingAddressId = jsonObject.getLong("billingAddressId", 0);

		if (billingAddressId == 0) {
			return null;
		}

		return accountEntry.getAccountEntryId() + "_" + billingAddressId;
	}

	@Override
	public AccountEntryValidatorResult validate(
			AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException {

		VIESAccountEntryValidatorConfiguration
			viesAccountEntryValidatorConfiguration =
				_getVIESAccountEntryValidatorConfiguration(
					accountEntry, jsonObject);

		if (viesAccountEntryValidatorConfiguration == null) {
			return null;
		}

		Address address = _addressLocalService.fetchAddress(
			jsonObject.getLong("billingAddressId", 0));

		if ((address == null) ||
			!Objects.equals(
				AccountEntry.class.getName(), address.getClassName()) ||
			(address.getClassPK() != accountEntry.getAccountEntryId())) {

			return null;
		}

		Country country = address.getCountry();

		if ((country == null) ||
			!ArrayUtil.contains(
				viesAccountEntryValidatorConfiguration.countryCodes(),
				country.getA2())) {

			return null;
		}

		String classPK = getClassPK(accountEntry, jsonObject);

		String taxIdNumber = accountEntry.getTaxIdNumber();

		if (Validator.isNull(taxIdNumber)) {
			return AccountEntryValidatorResult.builder(
				classPK
			).resultMessage(
				"account-missing-vat-code"
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_FAILURE
			).build();
		}

		VIESClient viesClient = new VIESClient();

		JSONObject checkVatNumberJSONObject = viesClient.checkVatNumber(
			accountEntry.getCompanyId(),
			JSONUtil.put(
				"countryCode", country.getA2()
			).put(
				"vatNumber", taxIdNumber
			));

		JSONArray errorWrappersJSONArray =
			checkVatNumberJSONObject.getJSONArray("errorWrappers");

		if ((errorWrappersJSONArray != null) &&
			(errorWrappersJSONArray.length() > 0)) {

			JSONObject errorWrapperJSONObject =
				errorWrappersJSONArray.getJSONObject(0);

			return _getAccountEntryValidatorResult(
				classPK, errorWrapperJSONObject.getString("error"));
		}

		if (checkVatNumberJSONObject.getBoolean("valid", false)) {
			return AccountEntryValidatorResult.builder(
				classPK
			).resultStatus(
				AccountEntryValidatorConstants.RESULT_SUCCESS
			).build();
		}

		return AccountEntryValidatorResult.builder(
			classPK
		).resultMessage(
			"vies-error-unexpected-error"
		).resultStatus(
			AccountEntryValidatorConstants.RESULT_FAILURE
		).build();
	}

	private AccountEntryValidatorResult _getAccountEntryValidatorResult(
		String classPK, String error) {

		String resultMessage = _resultMessages.getOrDefault(
			error, "vies-error-unexpected-error");

		String resultStatus = AccountEntryValidatorConstants.RESULT_WARNING;

		if (error.equals("INVALID_INPUT") || error.equals("VAT_BLOCKED")) {
			resultStatus = AccountEntryValidatorConstants.RESULT_FAILURE;
		}

		return AccountEntryValidatorResult.builder(
			classPK
		).resultMessage(
			resultMessage
		).resultStatus(
			resultStatus
		).build();
	}

	private VIESAccountEntryValidatorConfiguration
			_getVIESAccountEntryValidatorConfiguration(
				AccountEntry accountEntry, JSONObject jsonObject)
		throws PortalException {

		if ((accountEntry == null) || (jsonObject == null) ||
			!Objects.equals(
				accountEntry.getType(),
				AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS)) {

			return null;
		}

		VIESAccountEntryValidatorConfiguration
			viesAccountEntryValidatorConfiguration =
				(VIESAccountEntryValidatorConfiguration)
					getAccountEntryValidatorConfiguration(
						accountEntry.getCompanyId());

		if ((viesAccountEntryValidatorConfiguration == null) ||
			!viesAccountEntryValidatorConfiguration.enabled()) {

			return null;
		}

		return viesAccountEntryValidatorConfiguration;
	}

	private static final Map<String, String> _resultMessages =
		HashMapBuilder.put(
			"GLOBAL_MAX_CONCURRENT_REQ", "vies-error-unexpected-error"
		).put(
			"INVALID_INPUT", "vies-error-invalid-input"
		).put(
			"IO_ERROR", "vies-error-unexpected-error"
		).put(
			"IP_BLOCKED", "vies-error-unexpected-error"
		).put(
			"MS_MAX_CONCURRENT_REQ", "vies-error-unexpected-error"
		).put(
			"MS_UNAVAILABLE", "vies-error-unexpected-error"
		).put(
			"TECHNICAL_ERROR", "vies-error-unexpected-error"
		).put(
			"TIMEOUT", "vies-error-unexpected-error"
		).put(
			"VAT_BLOCKED", "vies-error-vat-blocked"
		).build();

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

}