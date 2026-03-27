/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Olivér Kecskeméty
 */
@ExtendedObjectClassDefinition(
	category = "privacy", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration",
	localization = "content/Language",
	name = "cookie-preference-handling-configuration-name"
)
public interface CookiesPreferenceHandlingConfiguration {

	@Meta.AD(
		deflt = "12", description = "cookie-consent-renewal-period-help",
		max = "12", min = "1", name = "cookie-consent-renewal-period",
		required = false
	)
	public int consentRenewalPeriod();

	@Meta.AD(
		deflt = "0", name = "custom-floating-icon-image-id", required = false
	)
	public long customFloatingIconImageId();

	@Meta.AD(
		deflt = "12", max = "12", min = "0",
		name = "cookie-dissent-renewal-period", required = false
	)
	public int dissentRenewalPeriod();

	@Meta.AD(
		deflt = "false", description = "cookie-enabled-help", name = "enabled",
		required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = "true",
		description = "cookie-explicit-consent-mode-help-deprecated",
		name = "cookie-explicit-consent-mode", required = false
	)
	public boolean explicitConsentMode();

	@Meta.AD(deflt = "cookie", name = "icon", required = false)
	public String floatingIcon();

	@Meta.AD(
		deflt = "true", description = "floating-icon-enabled-help",
		name = "floating-icon-enabled", required = false
	)
	public boolean floatingIconEnabled();

	@Meta.AD(deflt = "0", name = "modified-date", required = false)
	public long modifiedDate();

	@ExtendedAttributeDefinition(featureFlagKey = "LPD-75032")
	@Meta.AD(
		deflt = "false", description = "cookie-store-consent-help",
		name = "cookie-store-consent", required = false
	)
	public boolean storeConsent();

}