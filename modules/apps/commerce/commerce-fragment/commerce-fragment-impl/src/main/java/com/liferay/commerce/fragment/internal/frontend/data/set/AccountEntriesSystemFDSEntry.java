/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.frontend.data.set;

import com.liferay.commerce.fragment.internal.constants.CommerceFragmentFDSNames;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "frontend.data.set.name=" + CommerceFragmentFDSNames.ACCOUNT_ENTRIES,
	service = SystemFDSEntry.class
)
public class AccountEntriesSystemFDSEntry implements SystemFDSEntry {

	@Override
	public String getAdditionalAPIURLParameters() {
		return "filter=type in ({filter})&sort=name:asc";
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return CommerceFragmentFDSNames.ACCOUNT_ENTRIES;
	}

	@Override
	public String getRESTApplication() {
		return "/headless-commerce-delivery-catalog/v1.0";
	}

	@Override
	public String getRESTEndpoint() {
		return "/v1.0/channels/{channelId}/accounts";
	}

	@Override
	public String getRESTSchema() {
		return "Account";
	}

	@Override
	public String getTitle() {
		return _language.get(
			LocaleThreadLocal.getThemeDisplayLocale(), "accounts");
	}

	@Reference
	private Language _language;

}