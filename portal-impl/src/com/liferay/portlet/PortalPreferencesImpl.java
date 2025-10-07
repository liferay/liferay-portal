/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.PortalPreferenceValueUtil;
import com.liferay.portal.kernel.service.persistence.PortalPreferencesUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.simple.Element;
import com.liferay.portal.service.impl.PortalPreferenceValueLocalServiceImpl;

import java.io.IOException;
import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.StaleStateException;
import org.hibernate.exception.ConstraintViolationException;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class PortalPreferencesImpl
	implements Cloneable, PortalPreferences, Serializable {

	public static final TransactionConfig SUPPORTS_TRANSACTION_CONFIG;

	static {
		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setPropagation(Propagation.SUPPORTS);
		builder.setReadOnly(true);
		builder.setRollbackForClasses(
			PortalException.class, SystemException.class);

		SUPPORTS_TRANSACTION_CONFIG = builder.build();
	}

	public PortalPreferencesImpl() {
		this(0, 0, Collections.emptyMap(), false);
	}

	public PortalPreferencesImpl(
		long ownerId, int ownerType,
		Map<PortalPreferenceKey, String[]> preferences, boolean signedIn) {

		_ownerId = ownerId;
		_ownerType = ownerType;
		_signedIn = signedIn;

		_originalPreferences = preferences;
	}

	@Override
	public PortalPreferencesImpl clone() {
		Map<PortalPreferenceKey, String[]> preferences = _originalPreferences;

		if (_modifiedPreferences != null) {
			preferences = new HashMap<>(_modifiedPreferences);
		}

		return new PortalPreferencesImpl(
			getOwnerId(), getOwnerType(), preferences, isSignedIn());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortalPreferencesImpl)) {
			return false;
		}

		PortalPreferencesImpl portalPreferencesImpl =
			(PortalPreferencesImpl)object;

		if ((getOwnerId() == portalPreferencesImpl.getOwnerId()) &&
			(getOwnerType() == portalPreferencesImpl.getOwnerType()) &&
			Objects.equals(
				getPreferences(), portalPreferencesImpl.getPreferences())) {

			return true;
		}

		return false;
	}

	public Map<String, String[]> getMap(String namespace) {
		Map<PortalPreferenceKey, String[]> preferences = getPreferences();

		if (preferences.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, String[]> preferenceMap = new HashMap<>();

		for (Map.Entry<PortalPreferenceKey, String[]> entry :
				preferences.entrySet()) {

			PortalPreferenceKey portalPreferenceKey = entry.getKey();

			if (portalPreferenceKey.matchNamespace(namespace)) {
				preferenceMap.put(
					portalPreferenceKey.getKey(), entry.getValue());
			}
		}

		return preferenceMap;
	}

	public Enumeration<String> getNames(String namespace) {
		Map<String, String[]> preferences = getMap(namespace);

		return Collections.enumeration(preferences.keySet());
	}

	public long getOwnerId() {
		return _ownerId;
	}

	public int getOwnerType() {
		return _ownerType;
	}

	public Map<PortalPreferenceKey, String[]> getPreferences() {
		if (_modifiedPreferences != null) {
			return _modifiedPreferences;
		}

		return _originalPreferences;
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public String getValue(String namespace, String key) {
		return getValue(namespace, key, null);
	}

	@Override
	public String getValue(String namespace, String key, String defaultValue) {
		Map<PortalPreferenceKey, String[]> preferences = getPreferences();

		String[] values = preferences.get(
			new PortalPreferenceKey(namespace, key));

		if (PreferencesValueUtil.isNull(values)) {
			return defaultValue;
		}

		return PreferencesValueUtil.getActualValue(values[0]);
	}

	@Override
	public String[] getValues(String namespace, String key) {
		return getValues(namespace, key, null);
	}

	@Override
	public String[] getValues(
		String namespace, String key, String[] defaultValue) {

		return _getValues(
			new PortalPreferenceKey(namespace, key), defaultValue);
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, getOwnerId());

		hashCode = HashUtil.hash(hashCode, getOwnerType());
		hashCode = HashUtil.hash(hashCode, getPreferences());

		return hashCode;
	}

	@Override
	public boolean isSignedIn() {
		return _signedIn;
	}

	public void reset(String namespace, String key) {
		PortalPreferenceKey portalPreferenceKey = new PortalPreferenceKey(
			namespace, key);

		String[] values = _getValues(portalPreferenceKey, null);

		if (values == null) {
			return;
		}

		Runnable runnable = () -> {
			Map<PortalPreferenceKey, String[]> modifiedPreferences =
				_getModifiedPreferences();

			modifiedPreferences.remove(portalPreferenceKey);
		};

		try {
			_retryableStore(runnable, portalPreferenceKey);
		}
		catch (ConcurrentModificationException
					concurrentModificationException) {

			throw concurrentModificationException;
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	@Override
	public void resetValues(String namespace) {
		Map<PortalPreferenceKey, String[]> preferences = getPreferences();

		try {
			for (Map.Entry<PortalPreferenceKey, String[]> entry :
					preferences.entrySet()) {

				PortalPreferenceKey portalPreferenceKey = entry.getKey();

				if (portalPreferenceKey.matchNamespace(namespace)) {
					reset(namespace, portalPreferenceKey.getKey());
				}
			}
		}
		catch (ConcurrentModificationException
					concurrentModificationException) {

			throw concurrentModificationException;
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	@Override
	public void setSignedIn(boolean signedIn) {
		_signedIn = signedIn;
	}

	@Override
	public void setUserId(long userId) {
		_userId = userId;
	}

	@Override
	public void setValue(String namespace, String key, String value) {
		if (Validator.isNull(key) || key.equals(_RANDOM_KEY)) {
			return;
		}

		try {
			if (value == null) {
				reset(namespace, key);

				return;
			}

			PortalPreferenceKey portalPreferenceKey = new PortalPreferenceKey(
				namespace, key);

			String[] oldValues = _getValues(portalPreferenceKey, null);

			if ((oldValues != null) && (oldValues.length == 1) &&
				value.equals(oldValues[0])) {

				return;
			}

			Runnable runnable = () -> {
				Map<PortalPreferenceKey, String[]> modifiedPreferences =
					_getModifiedPreferences();

				modifiedPreferences.put(
					portalPreferenceKey,
					new String[] {PreferencesValueUtil.getXMLSafeValue(value)});
			};

			if (_signedIn) {
				_retryableStore(runnable, portalPreferenceKey);
			}
			else {
				runnable.run();
			}
		}
		catch (ConcurrentModificationException
					concurrentModificationException) {

			throw concurrentModificationException;
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	@Override
	public void setValues(String namespace, String key, String[] values) {
		if (Validator.isNull(key) || key.equals(_RANDOM_KEY)) {
			return;
		}

		try {
			if (values == null) {
				reset(namespace, key);

				return;
			}

			if (values.length == 1) {
				setValue(namespace, key, values[0]);

				return;
			}

			PortalPreferenceKey keyEntry = new PortalPreferenceKey(
				namespace, key);

			String[] oldValues = _getValues(keyEntry, null);

			if (oldValues != null) {
				Set<String> valuesSet = SetUtil.fromArray(values);
				Set<String> oldValuesSet = SetUtil.fromArray(oldValues);

				if (valuesSet.equals(oldValuesSet)) {
					return;
				}
			}

			Runnable runnable = () -> {
				Map<PortalPreferenceKey, String[]> modifiedPreferences =
					_getModifiedPreferences();

				modifiedPreferences.put(
					keyEntry, PreferencesValueUtil.getXMLSafeValues(values));
			};

			if (_signedIn) {
				_retryableStore(runnable, keyEntry);
			}
			else {
				runnable.run();
			}
		}
		catch (ConcurrentModificationException
					concurrentModificationException) {

			throw concurrentModificationException;
		}
		catch (Throwable throwable) {
			_log.error(throwable, throwable);
		}
	}

	@Override
	public int size() {
		Map<PortalPreferenceKey, String[]> preferences = getPreferences();

		return preferences.size();
	}

	public void store() throws IOException {
		try {
			PortalPreferencesLocalServiceUtil.updatePreferences(
				getOwnerId(), getOwnerType(), this);
		}
		catch (Throwable throwable) {
			throw new IOException(throwable);
		}
	}

	public String toXML() {
		Map<PortalPreferenceKey, String[]> preferences = getPreferences();

		if ((preferences == null) || preferences.isEmpty()) {
			return PortletConstants.DEFAULT_PREFERENCES;
		}

		Element portletPreferencesElement = new Element(
			"portlet-preferences", false);

		for (Map.Entry<PortalPreferenceKey, String[]> entry :
				preferences.entrySet()) {

			PortalPreferenceKey portalPreferenceKey = entry.getKey();

			Element preferenceElement = portletPreferencesElement.addElement(
				"preference");

			preferenceElement.addElement(
				"name", portalPreferenceKey.getNamespacedKey());

			for (String value : entry.getValue()) {
				preferenceElement.addElement("value", value);
			}
		}

		return portletPreferencesElement.toXMLString();
	}

	private Map<PortalPreferenceKey, String[]> _getModifiedPreferences() {
		if (_modifiedPreferences == null) {
			_modifiedPreferences = new ConcurrentHashMap<>(
				_originalPreferences);
		}

		return _modifiedPreferences;
	}

	private String[] _getValues(
		PortalPreferenceKey portalPreferenceKey, String[] def) {

		Map<PortalPreferenceKey, String[]> preferences = getPreferences();

		String[] values = preferences.get(portalPreferenceKey);

		if (PreferencesValueUtil.isNull(values)) {
			return def;
		}

		return PreferencesValueUtil.getActualValues(values);
	}

	private boolean _isCausedByConcurrentModification(Throwable throwable) {
		Throwable causeThrowable = throwable.getCause();

		while (throwable != causeThrowable) {
			if (throwable instanceof ConstraintViolationException ||
				throwable instanceof StaleStateException) {

				return true;
			}

			if (causeThrowable == null) {
				return false;
			}

			throwable = causeThrowable;

			causeThrowable = throwable.getCause();
		}

		return false;
	}

	private Map<PortalPreferenceKey, String[]> _reloadPreferenceMap() {
		com.liferay.portal.kernel.model.PortalPreferences portalPreferences =
			PortalPreferencesUtil.fetchByO_O(
				getOwnerId(), getOwnerType(), false);

		if (portalPreferences == null) {
			return null;
		}

		return PortalPreferenceValueLocalServiceImpl.getPreferenceMap(
			PortalPreferenceValueUtil.getPersistence(),
			portalPreferences.getPortalPreferencesId(), false);
	}

	private void _retryableStore(
			Runnable runnable, PortalPreferenceKey portalPreferenceKey)
		throws Throwable {

		String[] originalValues = _getValues(portalPreferenceKey, null);

		while (true) {
			try {
				runnable.run();

				store();

				return;
			}
			catch (Exception exception) {
				if (_isCausedByConcurrentModification(exception)) {
					Map<PortalPreferenceKey, String[]> preferenceMap =
						TransactionInvokerUtil.invoke(
							SUPPORTS_TRANSACTION_CONFIG,
							this::_reloadPreferenceMap);

					if (preferenceMap == null) {
						continue;
					}

					String[] values = preferenceMap.get(portalPreferenceKey);

					if (PreferencesValueUtil.isNull(values)) {
						values = null;
					}
					else {
						values = PreferencesValueUtil.getActualValues(values);
					}

					if (!Arrays.equals(originalValues, values)) {
						throw new ConcurrentModificationException();
					}

					_modifiedPreferences = null;

					_originalPreferences = preferenceMap;
				}
				else {
					throw exception;
				}
			}
		}
	}

	private static final String _RANDOM_KEY = "r";

	private static final Log _log = LogFactoryUtil.getLog(
		PortalPreferencesImpl.class);

	private Map<PortalPreferenceKey, String[]> _modifiedPreferences;
	private Map<PortalPreferenceKey, String[]> _originalPreferences;
	private final long _ownerId;
	private final int _ownerType;
	private boolean _signedIn;
	private long _userId;

}