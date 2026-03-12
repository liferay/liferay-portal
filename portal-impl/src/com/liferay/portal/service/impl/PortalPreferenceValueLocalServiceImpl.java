/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.internal.service.util.PortalPreferencesCacheUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.service.persistence.PortalPreferenceValuePersistence;
import com.liferay.portal.service.base.PortalPreferenceValueLocalServiceBaseImpl;
import com.liferay.portlet.PortalPreferenceKey;
import com.liferay.portlet.PortalPreferencesImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Preston Crary
 */
public class PortalPreferenceValueLocalServiceImpl
	extends PortalPreferenceValueLocalServiceBaseImpl {

	public static Map<PortalPreferenceKey, String[]> getPreferenceMap(
		PortalPreferenceValuePersistence portalPreferenceValuePersistence,
		long portalPreferencesId, boolean useFinderCache) {

		Map<PortalPreferenceKey, String[]> preferenceMap = null;

		if (useFinderCache) {
			preferenceMap = PortalPreferencesCacheUtil.get(portalPreferencesId);
		}

		if (preferenceMap != null) {
			return preferenceMap;
		}

		Map<PortalPreferenceKey, List<PortalPreferenceValue>>
			portalPreferenceValuesMap = getPortalPreferenceValuesMap(
				portalPreferenceValuePersistence, portalPreferencesId);

		preferenceMap = new HashMap<>();

		for (Map.Entry<PortalPreferenceKey, List<PortalPreferenceValue>> entry :
				portalPreferenceValuesMap.entrySet()) {

			List<PortalPreferenceValue> portalPreferenceValues =
				entry.getValue();

			String[] values = new String[portalPreferenceValues.size()];

			for (int i = 0; i < portalPreferenceValues.size(); i++) {
				PortalPreferenceValue portalPreferenceValue =
					portalPreferenceValues.get(i);

				values[i] = portalPreferenceValue.getValue();
			}

			preferenceMap.put(entry.getKey(), values);
		}

		PortalPreferencesCacheUtil.put(portalPreferencesId, preferenceMap);

		return preferenceMap;
	}

	public static Map<Long, Map<PortalPreferenceKey, String[]>>
		getPreferenceMaps(
			PortalPreferenceValuePersistence portalPreferenceValuePersistence,
			long[] portalPreferencesIds) {

		Map<Long, Map<PortalPreferenceKey, String[]>> preferenceMaps =
			new HashMap<>();

		boolean cacheMiss = false;

		for (long portalPreferencesId : portalPreferencesIds) {
			Map<PortalPreferenceKey, String[]> preferenceMap =
				PortalPreferencesCacheUtil.get(portalPreferencesId);

			if (preferenceMap == null) {
				cacheMiss = true;

				break;
			}

			preferenceMaps.put(portalPreferencesId, preferenceMap);
		}

		if (!cacheMiss) {
			return preferenceMaps;
		}

		preferenceMaps.clear();

		Map<Long, Map<PortalPreferenceKey, List<PortalPreferenceValue>>>
			portalPreferenceValuesMaps = getPortalPreferenceValuesMaps(
				portalPreferenceValuePersistence, portalPreferencesIds);

		for (Map.Entry
				<Long, Map<PortalPreferenceKey, List<PortalPreferenceValue>>>
					entry1 : portalPreferenceValuesMaps.entrySet()) {

			Map<PortalPreferenceKey, String[]> preferenceMap = new HashMap<>();

			Map<PortalPreferenceKey, List<PortalPreferenceValue>>
				portalPreferenceValuesMap = entry1.getValue();

			for (Map.Entry<PortalPreferenceKey, List<PortalPreferenceValue>>
					entry2 : portalPreferenceValuesMap.entrySet()) {

				List<PortalPreferenceValue> portalPreferenceValues =
					entry2.getValue();

				String[] values = new String[portalPreferenceValues.size()];

				for (int i = 0; i < portalPreferenceValues.size(); i++) {
					PortalPreferenceValue portalPreferenceValue =
						portalPreferenceValues.get(i);

					values[i] = portalPreferenceValue.getValue();
				}

				preferenceMap.put(entry2.getKey(), values);
			}

			preferenceMaps.put(entry1.getKey(), preferenceMap);

			PortalPreferencesCacheUtil.put(entry1.getKey(), preferenceMap);
		}

		return preferenceMaps;
	}

	@Override
	public com.liferay.portal.kernel.portlet.PortalPreferences
		getPortalPreferences(
			PortalPreferences portalPreferences, boolean signedIn) {

		Map<PortalPreferenceKey, String[]> preferenceMap = getPreferenceMap(
			portalPreferenceValuePersistence,
			portalPreferences.getPortalPreferencesId(), true);

		return new PortalPreferencesImpl(
			portalPreferences.getOwnerId(), portalPreferences.getOwnerType(),
			preferenceMap, signedIn);
	}

	protected static Map<PortalPreferenceKey, List<PortalPreferenceValue>>
		getPortalPreferenceValuesMap(
			PortalPreferenceValuePersistence portalPreferenceValuePersistence,
			long portalPreferencesId) {

		Map<PortalPreferenceKey, List<PortalPreferenceValue>>
			portalPreferenceValuesMap = new HashMap<>();

		for (PortalPreferenceValue portalPreferenceValue :
				portalPreferenceValuePersistence.findByPortalPreferencesId(
					portalPreferencesId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null, false)) {

			List<PortalPreferenceValue> portalPreferenceValues =
				portalPreferenceValuesMap.computeIfAbsent(
					new PortalPreferenceKey(
						portalPreferenceValue.getNamespace(),
						portalPreferenceValue.getKey()),
					key -> new ArrayList<>(1));

			portalPreferenceValues.add(portalPreferenceValue);
		}

		return portalPreferenceValuesMap;
	}

	protected static Map
		<Long, Map<PortalPreferenceKey, List<PortalPreferenceValue>>>
			getPortalPreferenceValuesMaps(
				PortalPreferenceValuePersistence
					portalPreferenceValuePersistence,
				long[] portalPreferencesIds) {

		Map<Long, Map<PortalPreferenceKey, List<PortalPreferenceValue>>>
			portalPreferenceValuesMaps = new HashMap<>();

		for (PortalPreferenceValue portalPreferenceValue :
				portalPreferenceValuePersistence.findByPortalPreferencesId(
					portalPreferencesIds, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null, false)) {

			Map<PortalPreferenceKey, List<PortalPreferenceValue>>
				portalPreferenceValuesMap =
					portalPreferenceValuesMaps.computeIfAbsent(
						portalPreferenceValue.getPortalPreferencesId(),
						key -> new HashMap<>());

			List<PortalPreferenceValue> portalPreferenceValues =
				portalPreferenceValuesMap.computeIfAbsent(
					new PortalPreferenceKey(
						portalPreferenceValue.getNamespace(),
						portalPreferenceValue.getKey()),
					key -> new ArrayList<>(1));

			portalPreferenceValues.add(portalPreferenceValue);
		}

		return portalPreferenceValuesMaps;
	}

}