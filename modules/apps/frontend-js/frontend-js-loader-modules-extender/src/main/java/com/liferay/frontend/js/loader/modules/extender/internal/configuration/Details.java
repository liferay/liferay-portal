/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Raymond Augé
 */
@ExtendedObjectClassDefinition(
	category = "infrastructure", deprecated = true, featureFlagKey = "LPD-48372"
)
@Meta.OCD(
	id = "com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details",
	localization = "content/Language", name = "details-configuration-name"
)
public @interface Details {

	public static final String CONFIG_JSON = "META-INF/config.json";

	public static final String CONTENT_TYPE = "text/javascript; charset=UTF-8";

	public static final int MAX_VALUE_LESS_1K = Integer.MAX_VALUE - 1000;

	public static final String OSGI_WEBRESOURCE = "osgi.webresource";

	@Meta.AD(deflt = "true", name = "apply-versioning", required = false)
	public boolean applyVersioning();

	@Meta.AD(deflt = "false", name = "explain-resolutions", required = false)
	public boolean explainResolutions();

	@Meta.AD(deflt = "false", name = "expose-global", required = false)
	public boolean exposeGlobal();

	@Meta.AD(
		deflt = "warn", name = "log-level",
		optionValues = {"off", "error", "warn", "info", "debug"},
		required = false
	)
	public String logLevel();

	@Meta.AD(
		deflt = "262144", description = "max-request-length-help",
		name = "max-request-length", required = false
	)
	public long maxRequestLength();

	@Meta.AD(deflt = "60", name = "wait-timeout", required = false)
	public int waitTimeout();

}