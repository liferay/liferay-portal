/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.link;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.site.pim.site.initializer.link.PIMLinkType;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Stefano Motta
 */
@Component(service = PIMLinkType.class)
public class VariantPIMLinkType implements PIMLinkType {

	public static final String TYPE = "variant";

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, TYPE);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean isClustered() {
		return true;
	}

}