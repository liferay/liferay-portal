/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.upgrade.v1_0_1;

import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.CompanyConstants;
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
		PortalPreferencesLocalService portalPreferencesLocalService) {

		_portalPreferencesLocalService = portalPreferencesLocalService;
	}

	@Override
	protected void doUpgrade() {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					CompanyConstants.SYSTEM,
					PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		PortalPreferencesImpl portalPreferencesImpl =
			portalPreferencesWrapper.getPortalPreferencesImpl();

		Enumeration<String> enumeration = portalPreferencesImpl.getNames(
			_OLD_NAMESPACE);

		while (enumeration.hasMoreElements()) {
			String featureFlag = enumeration.nextElement();

			String value = portalPreferencesImpl.getValue(
				_OLD_NAMESPACE, featureFlag);

			portalPreferencesImpl.reset(_OLD_NAMESPACE, featureFlag);

			portalPreferencesImpl.setValue(
				FeatureFlagConstants.PREFERENCE_NAMESPACE, featureFlag, value);
		}

		portalPreferencesImpl.setValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE,
			FeatureFlagConstants.PREFERENCE_KEY_DEPRECATION_PROCESSED, "true");

		_portalPreferencesLocalService.updatePreferences(
			CompanyConstants.SYSTEM, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
			portalPreferencesImpl);
	}

	private static final String _OLD_NAMESPACE =
		FeatureFlagConstants.PORTAL_PROPERTY_KEY_FEATURE_FLAG;

	private final PortalPreferencesLocalService _portalPreferencesLocalService;

}