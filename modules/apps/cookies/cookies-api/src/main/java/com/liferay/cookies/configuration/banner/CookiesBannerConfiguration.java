/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.configuration.banner;

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
	id = "com.liferay.cookies.configuration.banner.CookiesBannerConfiguration",
	localization = "content/Language", name = "cookie-banner-configuration-name"
)
public interface CookiesBannerConfiguration {

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:cookies-banner-title}", name = "title",
		required = false
	)
	public LocalizedValuesMap title();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:cookies-banner-content}", name = "content",
		required = false
	)
	public LocalizedValuesMap content();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(name = "privacy-policy-link", required = false)
	public String privacyPolicyLink();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "${language:visit-our-privacy-policy}",
		name = "link-display-text", required = false
	)
	public LocalizedValuesMap linkDisplayText();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "true", name = "include-decline-all-button", required = false
	)
	public boolean includeDeclineAllButton();

}