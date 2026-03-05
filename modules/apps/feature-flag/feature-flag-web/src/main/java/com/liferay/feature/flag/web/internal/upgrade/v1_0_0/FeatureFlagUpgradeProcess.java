/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portlet.PortalPreferencesImpl;
import com.liferay.portlet.PortalPreferencesWrapper;

import java.util.Enumeration;

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
		_companyLocalService.forEachCompanyId(
			companyId -> {
				PortalPreferencesWrapper portalPreferencesWrapper =
					(PortalPreferencesWrapper)
						_portalPreferencesLocalService.getPreferences(
							companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

				PortalPreferencesImpl portalPreferencesImpl =
					portalPreferencesWrapper.getPortalPreferencesImpl();

				Enumeration<String> enumeration =
					portalPreferencesImpl.getNames(_OLD_NAMESPACE);

				while (enumeration.hasMoreElements()) {
					String featureFlag = enumeration.nextElement();

					String value = portalPreferencesImpl.getValue(
						_OLD_NAMESPACE, featureFlag);

					portalPreferencesImpl.reset(_OLD_NAMESPACE, featureFlag);

					portalPreferencesImpl.setValue(
						FeatureFlagConstants.PREFERENCE_NAMESPACE, featureFlag,
						value);
				}

				portalPreferencesImpl.setValue(
					FeatureFlagConstants.PREFERENCE_NAMESPACE,
					FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED,
					"true");

				_portalPreferencesLocalService.updatePreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
					portalPreferencesImpl);
			});
	}

	private static final String _OLD_NAMESPACE =
		FeatureFlagConstants.PORTAL_PROPERTY_KEY_FEATURE_FLAG;

	private final CompanyLocalService _companyLocalService;
	private final PortalPreferencesLocalService _portalPreferencesLocalService;

}