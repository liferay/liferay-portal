/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.manager;

import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.PortalPreferencesWrapper;

/**
 * @author Drew Brokke
 */
public class FeatureFlagPreferencesManager {

	public FeatureFlagPreferencesManager(
		PortalPreferencesLocalService portalPreferencesLocalService) {

		_portalPreferencesLocalService = portalPreferencesLocalService;
	}

	public Boolean isEnabled(long companyId, String key) {
		if (Validator.isNull(
				_portalPreferencesLocalService.fetchPortalPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY))) {

			return null;
		}

		PortalPreferences portalPreferences = _getPortalPreferences(companyId);

		String value = portalPreferences.getValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE, key);

		if (value == null) {
			return null;
		}

		return GetterUtil.getBoolean(value);
	}

	public void setEnabled(long companyId, String key, boolean enabled) {
		PortalPreferences portalPreferences = _getPortalPreferences(companyId);

		portalPreferences.setValue(
			FeatureFlagConstants.PREFERENCE_NAMESPACE, key,
			String.valueOf(enabled));

		_portalPreferencesLocalService.updatePreferences(
			companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY, portalPreferences);
	}

	private PortalPreferences _getPortalPreferences(long companyId) {
		PortalPreferencesWrapper portalPreferencesWrapper =
			(PortalPreferencesWrapper)
				_portalPreferencesLocalService.getPreferences(
					companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return portalPreferencesWrapper.getPortalPreferencesImpl();
	}

	private final PortalPreferencesLocalService _portalPreferencesLocalService;

}