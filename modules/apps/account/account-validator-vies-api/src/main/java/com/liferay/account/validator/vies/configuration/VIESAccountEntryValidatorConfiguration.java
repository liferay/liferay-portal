/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.validator.vies.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.account.configuration.AccountEntryValidatorConfiguration;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Crescenzo Rega
 */
@ExtendedObjectClassDefinition(
	category = "accounts", generateUI = false,
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.account.validator.vies.configuration.VIESAccountEntryValidatorConfiguration",
	localization = "content/Language",
	name = "vies-account-entry-configuration-name"
)
public interface VIESAccountEntryValidatorConfiguration
	extends AccountEntryValidatorConfiguration {

	@Meta.AD(
		deflt = "15", description = "vies-check-interval-description",
		min = "1", name = "vies-check-interval", required = false
	)
	@Override
	public int checkInterval();

	@Meta.AD(
		deflt = "AT,BE,BG,CY,CZ,DE,DK,EE,ES,FI,FR,GR,HR,HU,IE,IT,LT,LU,LV,MT,NL,PL,PT,RO,SE,SI,SK",
		description = "vies-country-codes-description",
		name = "vies-country-codes", required = false
	)
	public String[] countryCodes();

	@Meta.AD(deflt = "true", name = "enabled", required = false)
	@Override
	public boolean enabled();

	@Meta.AD(
		deflt = "https://ec.europa.eu/taxation_customs/vies/rest-api/check-vat-number",
		description = "vies-endpoint-url-description",
		name = "vies-endpoint-url", required = false
	)
	public String viesEndpointURL();

}