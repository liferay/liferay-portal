/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v6_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.LocalizationUtil;

import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Date;

/**
 * @author Pei-Jung Lan
 */
public class CommerceCountryUpgradeProcess extends UpgradeProcess {

	public CommerceCountryUpgradeProcess(
		CountryLocalService countryLocalService) {

		_countryLocalService = countryLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (Statement selectStatement = connection.createStatement()) {
			ResultSet resultSet = selectStatement.executeQuery(
				"select * from CommerceCountry where twoLettersISOCode is " +
					"null or threeLettersISOCode is null or numericISOCode = " +
						"0");

			if (resultSet.next()) {
				throw new UpgradeException(
					"Unable to migrate data in CommerceCountry to Country " +
						"because it contains countries with missing ISO codes");
			}
		}

		try (Statement selectStatement = connection.createStatement()) {
			ResultSet resultSet = selectStatement.executeQuery(
				"select * from CommerceCountry order by commerceCountryId asc");

			while (resultSet.next()) {
				String a2 = resultSet.getString("twoLettersISOCode");
				String a3 = resultSet.getString("threeLettersISOCode");
				boolean active = resultSet.getBoolean("active_");
				boolean billingAllowed = resultSet.getBoolean("billingAllowed");
				boolean channelFilterEnabled = resultSet.getBoolean(
					"channelFilterEnabled");
				long commerceCountryId = resultSet.getLong("commerceCountryId");
				long companyId = resultSet.getLong("companyId");
				Date lastPublishDate = resultSet.getTimestamp(
					"lastPublishDate");
				String name = resultSet.getString("name");
				String numericISOCode = resultSet.getString("numericISOCode");
				Double priority = resultSet.getDouble("priority");
				boolean shippingAllowed = resultSet.getBoolean(
					"shippingAllowed");
				boolean subjectToVAT = resultSet.getBoolean("subjectToVAT");

				Country country = _countryLocalService.fetchCountryByA2(
					companyId, a2);

				if (country != null) {
					country = _updateCountry(
						country, a2, a3, active, billingAllowed,
						channelFilterEnabled, name, numericISOCode, priority,
						shippingAllowed, subjectToVAT, lastPublishDate);
				}
				else {
					country = _addCountry(
						commerceCountryId, companyId,
						resultSet.getLong("userId"),
						resultSet.getString("userName"),
						resultSet.getTimestamp("createDate"),
						resultSet.getTimestamp("modifiedDate"), a2, a3, active,
						billingAllowed, channelFilterEnabled, name,
						numericISOCode, priority, shippingAllowed, subjectToVAT,
						lastPublishDate);
				}

				if (country.getCountryId() == commerceCountryId) {
					continue;
				}

				for (String tableName : _TABLE_NAMES) {
					_updateCountryId(
						tableName, country.getCountryId(), commerceCountryId);
				}
			}
		}
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.dropTables("CommerceCountry")
		};
	}

	private Country _addCountry(
			long countryId, long companyId, long userId, String userName,
			Date createDate, Date modifiedDate, String a2, String a3,
			boolean active, boolean billingAllowed, boolean groupFilterEnabled,
			String name, String number, double position,
			boolean shippingAllowed, boolean subjectToVAT, Date lastPublishDate)
		throws Exception {

		if (_countryLocalService.fetchCountry(countryId) != null) {
			countryId = increment();
		}

		Country country = _countryLocalService.createCountry(countryId);

		country.setDefaultLanguageId(
			LocalizationUtil.getDefaultLanguageId(name));
		country.setCompanyId(companyId);
		country.setUserId(userId);
		country.setUserName(userName);
		country.setCreateDate(createDate);
		country.setModifiedDate(modifiedDate);
		country.setA2(a2);
		country.setA3(a3);
		country.setActive(active);
		country.setBillingAllowed(billingAllowed);
		country.setGroupFilterEnabled(groupFilterEnabled);
		country.setName(
			LocalizationUtil.getLocalization(
				name, LocalizationUtil.getDefaultLanguageId(name)));
		country.setNumber(number);
		country.setPosition(position);
		country.setShippingAllowed(shippingAllowed);
		country.setSubjectToVAT(subjectToVAT);
		country.setLastPublishDate(lastPublishDate);

		_countryLocalService.addCountry(country);

		for (String languageId :
				LocalizationUtil.getAvailableLanguageIds(name)) {

			_countryLocalService.updateCountryLocalization(
				country, languageId,
				LocalizationUtil.getLocalization(name, languageId));
		}

		return country;
	}

	private Country _updateCountry(
			Country country, String a2, String a3, boolean active,
			boolean billingAllowed, boolean groupFilterEnabled, String name,
			String number, double position, boolean shippingAllowed,
			boolean subjectToVAT, Date lastPublishDate)
		throws Exception {

		country.setDefaultLanguageId(
			LocalizationUtil.getDefaultLanguageId(name));
		country.setA2(a2);
		country.setA3(a3);
		country.setActive(active);
		country.setBillingAllowed(billingAllowed);
		country.setGroupFilterEnabled(groupFilterEnabled);
		country.setName(
			LocalizationUtil.getLocalization(
				name, LocalizationUtil.getDefaultLanguageId(name)));
		country.setNumber(number);
		country.setPosition(position);
		country.setShippingAllowed(shippingAllowed);
		country.setSubjectToVAT(subjectToVAT);
		country.setLastPublishDate(lastPublishDate);

		for (String languageId :
				LocalizationUtil.getAvailableLanguageIds(name)) {

			_countryLocalService.updateCountryLocalization(
				country, languageId,
				LocalizationUtil.getLocalization(name, languageId));
		}

		return _countryLocalService.updateCountry(country);
	}

	private void _updateCountryId(
			String tableName, long newCountryId, long oldCountryId)
		throws Exception {

		String columnName = "countryId";

		if (tableName.equals("CommerceRegion")) {
			columnName = "commerceCountryId";
		}

		runSQL(
			StringBundler.concat(
				"update ", tableName, " set ", columnName, " = ", newCountryId,
				" where ", columnName, " = ", oldCountryId));
	}

	private static final String[] _TABLE_NAMES = {
		"CommerceAddress", "CommerceAddressRestriction", "CommerceRegion",
		"CommerceTaxFixedRateAddressRel", "CShippingFixedOptionRel"
	};

	private final CountryLocalService _countryLocalService;

}