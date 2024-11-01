/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.upgrade.v1_0_0;

import com.liferay.feature.flag.web.internal.feature.flag.constants.FeatureFlagPortalPreferenceConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portlet.PortalPreferencesWrapper;

/**
 * @author Thiago Buarque
 */
public class FeatureFlagUpgradeProcess extends UpgradeProcess {

	public FeatureFlagUpgradeProcess(
		CompanyLocalService companyLocalService,
		PortalPreferencesLocalService portalPreferencesLocalService) {

		_companyLocalService = companyLocalService;
		_portalPreferencesLocalService = portalPreferencesLocalService;
	}

	@Override
	protected void doUpgrade() {
		System.out.println("Running upgrade!!!");
		_companyLocalService.forEachCompanyId(
			companyId -> {
				PortalPreferences portalPreferences = _getPortalPreferences(
					companyId);

				portalPreferences.setValue(
					FeatureFlagPortalPreferenceConstants.NAMESPACE,
					FeatureFlagPortalPreferenceConstants.PREFERENCE_KEY,
					"true");

				_portalPreferencesLocalService.updatePreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
					portalPreferences);
			});
	}

	private PortalPreferences _getPortalPreferences(long companyId) {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesWrapper.getPortalPreferencesImpl();
	}

	private final CompanyLocalService _companyLocalService;
	private final PortalPreferencesLocalService _portalPreferencesLocalService;

}