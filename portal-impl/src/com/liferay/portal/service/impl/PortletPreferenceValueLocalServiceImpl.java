/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.persistence.PortletPreferenceValuePersistence;
import com.liferay.portal.service.base.PortletPreferenceValueLocalServiceBaseImpl;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Preston Crary
 */
public class PortletPreferenceValueLocalServiceImpl
	extends PortletPreferenceValueLocalServiceBaseImpl {

	@Override
	public int getPortletPreferenceValuesCount(
		long companyId, String name, String smallValue) {

		return portletPreferenceValuePersistence.countByC_N_SV(
			companyId, name, smallValue);
	}

	@Override
	public jakarta.portlet.PortletPreferences getPreferences(
		PortletPreferences portletPreferences) {

		Map<String, List<PortletPreferenceValue>> portletPreferenceValuesMap =
			getPortletPreferenceValuesMap(
				portletPreferenceValuePersistence,
				portletPreferences.getPortletPreferencesId());

		Map<String, Preference> preferenceMap = new HashMap<>();

		for (Map.Entry<String, List<PortletPreferenceValue>> entry :
				portletPreferenceValuesMap.entrySet()) {

			String name = entry.getKey();

			List<PortletPreferenceValue> portletPreferenceValues =
				entry.getValue();

			String[] values = new String[portletPreferenceValues.size()];

			boolean readOnly = false;

			for (int i = 0; i < portletPreferenceValues.size(); i++) {
				PortletPreferenceValue portletPreferenceValue =
					portletPreferenceValues.get(i);

				values[i] = portletPreferenceValue.getValue();

				if (portletPreferenceValue.isReadOnly()) {
					readOnly = true;
				}
			}

			preferenceMap.put(name, new Preference(name, values, readOnly));
		}

		return new PortletPreferencesImpl(
			portletPreferences.getCompanyId(), portletPreferences.getOwnerId(),
			portletPreferences.getOwnerType(), portletPreferences.getPlid(),
			portletPreferences.getPortletId(), null, preferenceMap);
	}

	protected static Map<String, List<PortletPreferenceValue>>
		getPortletPreferenceValuesMap(
			PortletPreferenceValuePersistence portletPreferenceValuePersistence,
			long portletPreferencesId) {

		Map<String, List<PortletPreferenceValue>> portletPreferenceValuesMap =
			new HashMap<>();

		for (PortletPreferenceValue portletPreferenceValue :
				portletPreferenceValuePersistence.findByPortletPreferencesId(
					portletPreferencesId)) {

			List<PortletPreferenceValue> portletPreferenceValues =
				portletPreferenceValuesMap.computeIfAbsent(
					portletPreferenceValue.getName(),
					key -> new ArrayList<>(1));

			portletPreferenceValues.add(portletPreferenceValue);
		}

		return portletPreferenceValuesMap;
	}

}