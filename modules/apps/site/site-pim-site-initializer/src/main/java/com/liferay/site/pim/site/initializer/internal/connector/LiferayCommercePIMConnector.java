/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.connector;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
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
	public List<PIMConnectorChannelField> getPIMConnectorChannelFields(
		Locale locale) {

		return ListUtil.fromArray(
			new PIMConnectorChannelField(
				LanguageUtil.get(locale, "catalog-id"), false, "catalogId",
				true),
			new PIMConnectorChannelField(
				LanguageUtil.get(locale, "description"), false, "description",
				false),
			new PIMConnectorChannelField(
				LanguageUtil.get(locale, "name"), false, "name", true),
			new PIMConnectorChannelField(
				LanguageUtil.get(locale, "sku"), false, "skus[].sku", true),
			new PIMConnectorChannelField(
				LanguageUtil.get(locale, "tags"), true, "tags", false));
	}

	@Override
	public boolean isActive(long companyId) {
		return true;
	}

}