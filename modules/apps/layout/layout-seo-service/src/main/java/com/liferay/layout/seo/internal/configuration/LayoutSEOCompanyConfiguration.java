/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Cristina González
 */
@ExtendedObjectClassDefinition(
	category = "pages", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	description = "layout-seo-configuration-description",
	id = "com.liferay.layout.seo.internal.configuration.LayoutSEOCompanyConfiguration",
	localization = "content/Language", name = "layout-seo-configuration-name"
)
public interface LayoutSEOCompanyConfiguration {

	/**
	 * Sets the configuration type to use with the localized URL.
	 */
	@Meta.AD(
		deflt = "default-language-url",
		description = "layout-seo-configuration-canonical-url-description",
		name = "canonical-url",
		optionLabels = {
			"layout-seo-configuration-page-default-language-url",
			"layout-seo-configuration-page-localized-url"
		},
		optionValues = {"default-language-url", "localized-url"},
		required = false
	)
	public String canonicalURL();

	/**
	 * Sets the configuration to allow the site admins to configure if hreflang
	 * tags in pages are filled only for the translated languages.
	 *
	 * @review
	 */
	@Meta.AD(
		deflt = "false",
		description = "layout-seo-configuration-enable-only-translated-hreflang-description",
		name = "layout-seo-configuration-enable-only-translated-hreflang",
		required = false
	)
	public boolean enableLayoutTranslatedLanguages();

	/**
	 * Sets if open graph is enabled.
	 */
	@Meta.AD(
		deflt = "true",
		description = "layout-seo-configuration-enable-open-graph-description",
		name = "enable-open-graph", required = false
	)
	public boolean enableOpenGraph();

}