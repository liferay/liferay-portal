/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.configuration.consent;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Olivér Kecskeméty
 */
@ExtendedObjectClassDefinition(
	category = "privacy", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.cookies.configuration.consent.CookiesConsentConfiguration",
	localization = "content/Language",
	name = "cookie-consent-panel-configuration-name"
)
public interface CookiesConsentConfiguration {

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(name = "cookie-policy-link", required = false)
	public String cookiePolicyLink();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:cookies-consent-description}", name = "description",
		required = false
	)
	public LocalizedValuesMap description();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:cookies-description[CONSENT_TYPE_FUNCTIONAL]}",
		name = "functional-cookies-description-field", required = false
	)
	public LocalizedValuesMap functionalCookiesDescription();

	@ExtendedAttributeDefinition(
		featureFlagKey = "LPD-51356", requiredInput = true
	)
	@Meta.AD(deflt = "false", name = "hide-from-end-user", required = false)
	public boolean functionalCookiesHideFromEndUser();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(deflt = "false", name = "prechecked", required = false)
	public boolean functionalCookiesPrechecked();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:visit-our-cookie-policy}",
		name = "link-display-text", required = false
	)
	public LocalizedValuesMap linkDisplayText();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:cookies-description[CONSENT_TYPE_PERFORMANCE]}",
		name = "performance-cookies-description-field", required = false
	)
	public LocalizedValuesMap performanceCookiesDescription();

	@ExtendedAttributeDefinition(
		featureFlagKey = "LPD-51356", requiredInput = true
	)
	@Meta.AD(deflt = "false", name = "hide-from-end-user", required = false)
	public boolean performanceCookiesHideFromEndUser();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(deflt = "false", name = "prechecked", required = false)
	public boolean performanceCookiesPrechecked();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:cookies-description[CONSENT_TYPE_PERSONALIZATION]}",
		name = "personalization-cookies-description-field", required = false
	)
	public LocalizedValuesMap personalizationCookiesDescription();

	@ExtendedAttributeDefinition(
		featureFlagKey = "LPD-51356", requiredInput = true
	)
	@Meta.AD(deflt = "false", name = "hide-from-end-user", required = false)
	public boolean personalizationCookiesHideFromEndUser();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(deflt = "false", name = "prechecked", required = false)
	public boolean personalizationCookiesPrechecked();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:cookies-description[CONSENT_TYPE_NECESSARY]}",
		name = "strictly-necessary-cookies-description-field", required = false
	)
	public LocalizedValuesMap strictlyNecessaryCookiesDescription();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:cookie-configuration}", name = "title",
		required = false
	)
	public LocalizedValuesMap title();

}