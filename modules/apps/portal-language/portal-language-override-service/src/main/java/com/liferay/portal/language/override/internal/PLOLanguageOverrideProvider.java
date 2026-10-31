/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal;

import com.liferay.portal.language.LanguageOverrideProvider;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(service = LanguageOverrideProvider.class)
public class PLOLanguageOverrideProvider implements LanguageOverrideProvider {

	@Override
	public ResourceBundle getOverrideResourceBundle(
		long companyId, Locale locale) {

		return _ploOverrideResourceBundleManager.getOverrideResourceBundle(
			companyId, locale);
	}

	@Reference
	private PLOOverrideResourceBundleManager _ploOverrideResourceBundleManager;

}