/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.shortcut.internal.upgrade.v2_0_0;

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leslie Wong
 */
public class OAuth2ApplicationAnalyticsCloudUpgradeProcess
	extends UpgradeProcess {

	public OAuth2ApplicationAnalyticsCloudUpgradeProcess(
		CompanyLocalService companyLocalService,
		OAuth2ApplicationLocalService oAuth2ApplicationLocalService) {

		_companyLocalService = companyLocalService;
		_oAuth2ApplicationLocalService = oAuth2ApplicationLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> _upgradeCompany(companyId));
	}

	private void _upgradeCompany(long companyId) {
		OAuth2Application oAuth2Application =
			_oAuth2ApplicationLocalService.
				fetchOAuth2ApplicationByExternalReferenceCode(
					"ANALYTICS-CLOUD", companyId);

		if (oAuth2Application == null) {
			return;
		}

		oAuth2Application.setHomePageURL("https://ldp.liferay.com");

		List<String> redirectURIsList = new ArrayList<>(
			oAuth2Application.getRedirectURIsList());

		if (!redirectURIsList.contains(
				"https://ldp.liferay.com/oauth/receive")) {

			redirectURIsList.add("https://ldp.liferay.com/oauth/receive");

			oAuth2Application.setRedirectURIsList(redirectURIsList);
		}

		_oAuth2ApplicationLocalService.updateOAuth2Application(
			oAuth2Application);
	}

	private final CompanyLocalService _companyLocalService;
	private final OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}