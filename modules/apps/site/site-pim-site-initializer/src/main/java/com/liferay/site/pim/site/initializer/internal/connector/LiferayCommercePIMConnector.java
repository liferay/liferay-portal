/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Andrea Sbarra
 */
@Component(service = PIMConnector.class)
public class LiferayCommercePIMConnector implements PIMConnector {

	public static final String KEY = "liferay-commerce";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(locale, KEY);
	}

	@Override
	public boolean isActive(long companyId) {
		return true;
	}

}