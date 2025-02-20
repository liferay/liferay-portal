/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sam Ziemer
 */
public class CategorizationSectionDisplayContext
	extends BaseSectionDisplayContext {

	public CategorizationSectionDisplayContext(
		CMSSiteInitializerConfiguration cmsSiteInitializerConfiguration,
		HttpServletRequest httpServletRequest) {

		super(cmsSiteInitializerConfiguration, httpServletRequest);
	}

	@Override
	public String[] getEntryClassNames() {
		return cmsSiteInitializerConfiguration.categorizationClassNames();
	}

}