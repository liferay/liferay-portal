/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.internal.service.util.PortalPreferencesCacheUtil;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalService;
import com.liferay.portal.kernel.service.persistence.PortalPreferenceValuePersistence;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.base.PortalPreferencesLocalServiceBaseImpl;
import com.liferay.portlet.PortalPreferenceKey;
import com.liferay.portlet.PortalPreferencesImpl;
import com.liferay.portlet.PortalPreferencesWrapper;

import jakarta.portlet.PortletPreferences;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Chow
 */
public class PortalPreferencesLocalServiceImpl
	extends PortalPreferencesLocalServiceBaseImpl {

	@Override
	public PortalPreferences addPortalPreferences(
		long ownerId, int ownerType, String defaultPreferences) {

		PortalPreferences previousPortalPreferences = fetchPortalPreferences(
			ownerId, ownerType);

		if (previousPortalPreferences != null) {
			throw new IllegalArgumentException(
				"Duplicate owner ID and owner type exists in " +
					previousPortalPreferences);
		}

		long portalPreferencesId = counterLocalService.increment();

		PortalPreferences portalPreferences =
			portalPreferencesPersistence.create(portalPreferencesId);

		portalPreferences.setOwnerId(ownerId);
		portalPreferences.setOwnerType(ownerType);

		if (Validator.isNull(defaultPreferences)) {
			defaultPreferences = PortletConstants.DEFAULT_PREFERENCES;
		}

		if (Objects.equals(
				PortletConstants.DEFAULT_PREFERENCES, defaultPreferences)) {

			PortalPreferencesCacheUtil.put(
				portalPreferencesId, Collections.emptyMap());
		}
		else {
			PortalPreferencesImpl portalPreferencesImpl =
				(PortalPreferencesImpl)PortletPreferencesFactoryUtil.fromXML(
					ownerId, ownerType, defaultPreferences);

			_updatePortalPreferences(
				portalPreferences, Collections.emptyMap(),
				portalPreferencesImpl.getPreferences());
		}

		try {
			portalPreferences = portalPreferencesPersistence.update(
				portalPreferences);
		}
		catch (SystemException systemException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Add failed, fetch {ownerId=", ownerId, ", ownerType=",
						ownerType, "}"));
			}

			portalPreferences = portalPreferencesPersistence.fetchByO_O(
				ownerId, ownerType, false);

			if (portalPreferences == null) {
				throw systemException;
			}
		}

		return portalPreferences;
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public PortalPreferences deletePortalPreferences(long portalPreferencesId)
		throws PortalException {

		_portalPreferenceValuePersistence.removeByPortalPreferencesId(
			portalPreferencesId);

		return super.deletePortalPreferences(portalPreferencesId);
	}

	@Indexable(type = IndexableType.DELETE)
	@Override
	public PortalPreferences deletePortalPreferences(
		PortalPreferences portalPreferences) {

		_portalPreferenceValuePersistence.removeByPortalPreferencesId(
			portalPreferences.getPortalPreferencesId());

		return super.deletePortalPreferences(portalPreferences);
	}

	@Override
	public PortalPreferences fetchPortalPreferences(
		long ownerId, int ownerType) {

		if (ownerType == PortletKeys.PREFS_OWNER_TYPE_COMPANY) {

			// This is counterintuitive but it is actually better for
			// performance. See LPS-196350 and
			// 2cd9801d2a243ecbc5c1025b614c9300ce53627d.

			for (PortalPreferences portalPreferences :
					portalPreferencesPersistence.findByOwnerType(
						PortletKeys.PREFS_OWNER_TYPE_COMPANY)) {

				if (portalPreferences.getOwnerId() == ownerId) {
					return portalPreferences;
				}
			}

			return null;
		}

		return portalPreferencesPersistence.fetchByO_O(ownerId, ownerType);
	}

	@Override
	public PortletPreferences getPreferences(long ownerId, int ownerType) {
		return getPreferences(ownerId, ownerType, null);
	}

	@Override
	public PortletPreferences getPreferences(
		long ownerId, int ownerType, String defaultPreferences) {

		if (ownerType == PortletKeys.PREFS_OWNER_TYPE_COMPANY) {
			return _getCompanyPreferences(ownerId, defaultPreferences);
		}

		PortalPreferences portalPreferences = fetchPortalPreferences(
			ownerId, ownerType);

		if (portalPreferences == null) {
			try {
				portalPreferences =
					portalPreferencesLocalService.addPortalPreferences(
						ownerId, ownerType, defaultPreferences);
			}
			catch (Throwable throwable) {
				if (_log.isDebugEnabled()) {
					_log.debug(throwable);
				}

				portalPreferences = fetchPortalPreferences(ownerId, ownerType);
			}
		}

		PortalPreferencesImpl portalPreferencesImpl =
			(PortalPreferencesImpl)
				_portalPreferenceValueLocalService.getPortalPreferences(
					portalPreferences, false);

		return new PortalPreferencesWrapper(portalPreferencesImpl);
	}

	@Override
	public PortalPreferences updatePreferences(
		long ownerId, int ownerType,
		com.liferay.portal.kernel.portlet.PortalPreferences portalPreferences) {

		PortalPreferencesImpl portalPreferencesImpl =
			(PortalPreferencesImpl)portalPreferences;

		return _updatePortalPreferences(
			ownerId, ownerType, portalPreferencesImpl.getPreferences());
	}

	@Override
	public PortalPreferences updatePreferences(
		long ownerId, int ownerType, String xml) {

		PortalPreferencesImpl portalPreferencesImpl =
			(PortalPreferencesImpl)PortletPreferencesFactoryUtil.fromXML(
				ownerId, ownerType, xml);

		return _updatePortalPreferences(
			ownerId, ownerType, portalPreferencesImpl.getPreferences());
	}

	private PortletPreferences _getCompanyPreferences(
		long ownerId, String defaultPreferences) {

		PortalPreferences portalPreferences = null;

		for (PortalPreferences curPortalPreferences :
				portalPreferencesPersistence.findByOwnerType(
					PortletKeys.PREFS_OWNER_TYPE_COMPANY)) {

			if (curPortalPreferences.getOwnerId() == ownerId) {
				portalPreferences = curPortalPreferences;

				break;
			}
		}

		if (portalPreferences == null) {
			try {
				portalPreferences =
					portalPreferencesLocalService.addPortalPreferences(
						ownerId, PortletKeys.PREFS_OWNER_TYPE_COMPANY,
						defaultPreferences);
			}
			catch (Throwable throwable) {
				if (_log.isDebugEnabled()) {
					_log.debug(throwable);
				}

				portalPreferences = fetchPortalPreferences(
					ownerId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);
			}
		}

		PortalPreferencesImpl portalPreferencesImpl =
			(PortalPreferencesImpl)
				_portalPreferenceValueLocalService.getPortalPreferences(
					portalPreferences, false);

		return new PortalPreferencesWrapper(portalPreferencesImpl);
	}

	private PortalPreferences _updatePortalPreferences(
		long ownerId, int ownerType,
		Map<PortalPreferenceKey, String[]> preferencesMap) {

		PortalPreferences portalPreferencesModel = fetchPortalPreferences(
			ownerId, ownerType);

		Map<PortalPreferenceKey, List<PortalPreferenceValue>>
			portalPreferenceValuesMap = Collections.emptyMap();

		if (portalPreferencesModel == null) {
			long portalPreferencesId = counterLocalService.increment();

			portalPreferencesModel = portalPreferencesPersistence.create(
				portalPreferencesId);

			portalPreferencesModel.setOwnerId(ownerId);
			portalPreferencesModel.setOwnerType(ownerType);

			portalPreferencesModel = portalPreferencesPersistence.update(
				portalPreferencesModel);
		}
		else {
			portalPreferenceValuesMap =
				PortalPreferenceValueLocalServiceImpl.
					getPortalPreferenceValuesMap(
						_portalPreferenceValuePersistence,
						portalPreferencesModel.getPortalPreferencesId());
		}

		_updatePortalPreferences(
			portalPreferencesModel, portalPreferenceValuesMap, preferencesMap);

		return portalPreferencesModel;
	}

	private void _updatePortalPreferences(
		PortalPreferences portalPreferences,
		Map<PortalPreferenceKey, List<PortalPreferenceValue>>
			portalPreferenceValueMap,
		Map<PortalPreferenceKey, String[]> preferencesMap) {

		List<Map.Entry<List<PortalPreferenceValue>, PortalPreference>>
			preferenceEntries = new ArrayList<>(preferencesMap.size());

		int newCount = 0;

		for (Map.Entry<PortalPreferenceKey, String[]> entry :
				preferencesMap.entrySet()) {

			String[] values = entry.getValue();

			if (values == null) {
				continue;
			}

			int size = 0;

			List<PortalPreferenceValue> portalPreferenceValues =
				portalPreferenceValueMap.remove(entry.getKey());

			if (portalPreferenceValues != null) {
				size = portalPreferenceValues.size();
			}

			if (values.length > size) {
				newCount += values.length - size;
			}

			preferenceEntries.add(
				new AbstractMap.SimpleImmutableEntry<>(
					portalPreferenceValues,
					new PortalPreference(entry.getKey(), values)));
		}

		for (List<PortalPreferenceValue> portalPreferenceValues :
				portalPreferenceValueMap.values()) {

			for (PortalPreferenceValue portalPreferenceValue :
					portalPreferenceValues) {

				_portalPreferenceValuePersistence.remove(portalPreferenceValue);
			}
		}

		long batchCounter = 0;

		if (newCount > 0) {
			batchCounter = counterLocalService.increment(
				PortalPreferenceValue.class.getName(), newCount);

			batchCounter -= newCount;
		}

		for (Map.Entry<List<PortalPreferenceValue>, PortalPreference> entry :
				preferenceEntries) {

			List<PortalPreferenceValue> portalPreferenceValues = entry.getKey();

			PortalPreference portalPreference = entry.getValue();

			PortalPreferenceKey portalPreferenceKey =
				portalPreference._portalPreferenceKey;
			String[] newValues = portalPreference._values;

			int oldSize = 0;

			if (portalPreferenceValues != null) {
				oldSize = portalPreferenceValues.size();
			}

			for (int i = 0; i < newValues.length; i++) {
				String value = newValues[i];

				if (oldSize > i) {
					PortalPreferenceValue portalPreferenceValue =
						portalPreferenceValues.get(i);

					if (!Objects.equals(
							newValues[i], portalPreferenceValue.getValue())) {

						portalPreferenceValue.setValue(value);

						_portalPreferenceValuePersistence.update(
							portalPreferenceValue);
					}
				}
				else {
					PortalPreferenceValue portalPreferenceValue =
						_portalPreferenceValuePersistence.create(
							++batchCounter);

					if (portalPreferences.getOwnerType() ==
							PortletKeys.PREFS_OWNER_TYPE_COMPANY) {

						portalPreferenceValue.setCompanyId(
							portalPreferences.getOwnerId());
					}

					portalPreferenceValue.setPortalPreferencesId(
						portalPreferences.getPortalPreferencesId());
					portalPreferenceValue.setIndex(i);
					portalPreferenceValue.setKey(portalPreferenceKey.getKey());
					portalPreferenceValue.setNamespace(
						portalPreferenceKey.getNamespace());
					portalPreferenceValue.setValue(value);

					_portalPreferenceValuePersistence.update(
						portalPreferenceValue);
				}
			}

			for (int i = newValues.length; i < oldSize; i++) {
				_portalPreferenceValuePersistence.remove(
					portalPreferenceValues.get(i));
			}
		}

		PortalPreferencesCacheUtil.put(
			portalPreferences.getPortalPreferencesId(), preferencesMap);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalPreferencesLocalServiceImpl.class);

	@BeanReference(type = PortalPreferenceValueLocalService.class)
	private PortalPreferenceValueLocalService
		_portalPreferenceValueLocalService;

	@BeanReference(type = PortalPreferenceValuePersistence.class)
	private PortalPreferenceValuePersistence _portalPreferenceValuePersistence;

	private static class PortalPreference {

		private PortalPreference(
			PortalPreferenceKey portalPreferenceKey, String[] values) {

			_portalPreferenceKey = portalPreferenceKey;
			_values = values;
		}

		private final PortalPreferenceKey _portalPreferenceKey;
		private final String[] _values;

	}

}