/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Iván Zaera Avellón
 */
@ExtendedObjectClassDefinition(
	category = "infrastructure",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY, strictScope = true
)
@Meta.OCD(
	id = "com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration",
	localization = "content/Language",
	name = "frontend-caching-configuration-name"
)
public interface FrontendCachingConfiguration {

	@Meta.AD(
		deflt = "do-not-use-hashes", description = "caching-strategy-help",
		name = "caching-strategy",
		optionLabels = {
			"do-not-use-hashes", "use-one-hash-per-file",
			"use-one-hash-per-web-context"
		},
		optionValues = {
			"do-not-use-hashes", "use-one-hash-per-file",
			"use-one-hash-per-web-context"
		},
		required = false
	)
	public String cachingStrategy();

	@Meta.AD(
		deflt = "use-system-defaults",
		description = "compression-strategy-help",
		name = "compression-strategy",
		optionLabels = {
			"do-not-compress", "use-best-speed-compression",
			"use-intermediate-compression", "use-maximum-compression",
			"use-system-defaults"
		},
		optionValues = {
			"do-not-compress", "use-best-speed-compression",
			"use-intermediate-compression", "use-maximum-compression",
			"use-system-defaults"
		},
		required = false
	)
	public String compressionStrategy();

	@Meta.AD(
		deflt = "86400", description = "css-style-sheets-max-age-help",
		name = "css-style-sheets-max-age", required = false
	)
	public long cssStyleSheetsMaxAge();

	@Meta.AD(
		deflt = "86400", description = "js-files-max-age-help",
		name = "js-files-max-age", required = false
	)
	public long jsFilesMaxAge();

	@Meta.AD(
		deflt = "3600", description = "labels-modules-max-age-help",
		name = "labels-modules-max-age", required = false
	)
	public long labelsModulesMaxAge();

	@Meta.AD(
		deflt = "false",
		description = "send-no-cache-for-css-style-sheets-help",
		name = "send-no-cache-for-css-style-sheets", required = false
	)
	public boolean sendNoCacheForCSSStyleSheets();

	@Meta.AD(
		deflt = "false", description = "send-no-cache-for-js-files-help",
		name = "send-no-cache-for-js-files", required = false
	)
	public boolean sendNoCacheForJSFiles();

	@Meta.AD(
		deflt = "false", description = "send-no-cache-for-labels-modules-help",
		name = "send-no-cache-for-labels-modules", required = false
	)
	public boolean sendNoCacheForLabelsModules();

	@Meta.AD(
		deflt = "false",
		description = "send-no-cache-for-tokenized-css-style-sheets-help",
		name = "send-no-cache-for-tokenized-css-style-sheets", required = false
	)
	public boolean sendNoCacheForTokenizedCSSStyleSheets();

	@Meta.AD(
		deflt = "false",
		description = "send-no-cache-for-translated-js-files-help",
		name = "send-no-cache-for-translated-js-files", required = false
	)
	public boolean sendNoCacheForTranslatedJSFiles();

	@Meta.AD(
		deflt = "86400",
		description = "tokenized-css-style-sheets-max-age-help",
		name = "tokenized-css-style-sheets-max-age", required = false
	)
	public long tokenizedCSSStyleSheetsMaxAge();

	@Meta.AD(
		deflt = "3600", description = "translated-js-files-max-age-help",
		name = "translated-js-files-max-age", required = false
	)
	public long translatedJSFilesMaxAge();

}