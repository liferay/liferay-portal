/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.xml.simple.Element;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PreferencesValidator;
import jakarta.portlet.ReadOnlyException;
import jakarta.portlet.ValidatorException;

import java.io.IOException;
import java.io.Serializable;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class PortletPreferencesImpl
	implements Cloneable, PortletPreferences, Serializable {

	public PortletPreferencesImpl() {
		this(0, 0, 0, 0, null, null, Collections.emptyMap());
	}

	public PortletPreferencesImpl(
		long companyId, long ownerId, int ownerType, long plid,
		String portletId, String xml, Map<String, Preference> preferences) {

		_companyId = companyId;
		_ownerId = ownerId;
		_ownerType = ownerType;
		_plid = plid;
		_portletId = portletId;

		_originalXML = xml;
		_originalPreferences = preferences;
	}

	public PortletPreferencesImpl(
		String xml, Map<String, Preference> preferences) {

		this(0, 0, 0, 0, null, xml, preferences);
	}

	@Override
	public Object clone() {
		return new PortletPreferencesImpl(
			getCompanyId(), getOwnerId(), getOwnerType(), _plid, _portletId,
			getOriginalXML(), getOriginalPreferences());
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PortletPreferencesImpl)) {
			return false;
		}

		PortletPreferencesImpl portletPreferencesImpl =
			(PortletPreferencesImpl)object;

		if ((getCompanyId() == portletPreferencesImpl.getCompanyId()) &&
			(getOwnerId() == portletPreferencesImpl.getOwnerId()) &&
			(getOwnerType() == portletPreferencesImpl.getOwnerType()) &&
			(getPlid() == portletPreferencesImpl.getPlid()) &&
			Objects.equals(
				getPortletId(), portletPreferencesImpl.getPortletId()) &&
			Objects.equals(
				getPreferences(), portletPreferencesImpl.getPreferences())) {

			return true;
		}

		return false;
	}

	@Override
	public Map<String, String[]> getMap() {
		Map<String, Preference> preferences = getPreferences();

		if (preferences.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<String, String[]> map = new HashMap<>();

		for (Map.Entry<String, Preference> entry : preferences.entrySet()) {
			Preference preference = entry.getValue();

			map.put(
				entry.getKey(),
				PreferencesValueUtil.getActualValues(preference.getValues()));
		}

		return map;
	}

	@Override
	public Enumeration<String> getNames() {
		Map<String, Preference> preferences = getPreferences();

		return Collections.enumeration(preferences.keySet());
	}

	public long getOwnerId() {
		return _ownerId;
	}

	public int getOwnerType() {
		return _ownerType;
	}

	public long getPlid() {
		return _plid;
	}

	public Map<String, Preference> getPreferences() {
		if (_modifiedPreferences != null) {
			return _modifiedPreferences;
		}

		return _originalPreferences;
	}

	@Override
	public String getValue(String key, String def) {
		if (key == null) {
			throw new IllegalArgumentException();
		}

		Map<String, Preference> preferences = getPreferences();

		Preference preference = preferences.get(key);

		if (preference == null) {
			return def;
		}

		String[] values = preference.getValues();

		if (PreferencesValueUtil.isNull(values)) {
			return def;
		}

		return PreferencesValueUtil.getActualValue(values[0]);
	}

	@Override
	public String[] getValues(String key, String[] def) {
		if (key == null) {
			throw new IllegalArgumentException();
		}

		Map<String, Preference> preferences = getPreferences();

		Preference preference = preferences.get(key);

		if (preference == null) {
			return def;
		}

		String[] values = preference.getValues();

		if (PreferencesValueUtil.isNull(values)) {
			return def;
		}

		return PreferencesValueUtil.getActualValues(values);
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, getCompanyId());

		hashCode = HashUtil.hash(hashCode, getOwnerId());
		hashCode = HashUtil.hash(hashCode, getOwnerType());
		hashCode = HashUtil.hash(hashCode, getPlid());
		hashCode = HashUtil.hash(hashCode, getPortletId());
		hashCode = HashUtil.hash(hashCode, getPreferences());

		return hashCode;
	}

	@Override
	public boolean isReadOnly(String key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}

		Map<String, Preference> preferences = getPreferences();

		Preference preference = preferences.get(key);

		if ((preference != null) && preference.isReadOnly()) {
			return true;
		}

		return false;
	}

	@Override
	public void reset(String key) throws ReadOnlyException {
		if (isReadOnly(key)) {
			throw new ReadOnlyException(key);
		}

		if ((_defaultPortletPreferences == null) && (_portletId != null)) {
			try {
				_defaultPortletPreferences =
					PortletPreferencesLocalServiceUtil.getDefaultPreferences(
						_companyId, _portletId);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}
		}

		String[] defaultValues = null;

		if (_defaultPortletPreferences != null) {
			defaultValues = _defaultPortletPreferences.getValues(
				key, defaultValues);
		}

		if (defaultValues != null) {
			setValues(key, defaultValues);
		}
		else {
			Map<String, Preference> modifiedPreferences =
				getModifiedPreferences();

			modifiedPreferences.remove(key);
		}
	}

	public void setPlid(long plid) {
		_plid = plid;
	}

	@Override
	public void setValue(String key, String value) throws ReadOnlyException {
		if (key == null) {
			throw new IllegalArgumentException();
		}

		value = PreferencesValueUtil.getXMLSafeValue(value);

		Map<String, Preference> modifiedPreferences = getModifiedPreferences();

		Preference preference = modifiedPreferences.get(key);

		if ((preference != null) && preference.isReadOnly()) {
			throw new ReadOnlyException(key);
		}

		modifiedPreferences.put(key, new Preference(key, value));
	}

	@Override
	public void setValues(String key, String... values)
		throws ReadOnlyException {

		if (key == null) {
			throw new IllegalArgumentException();
		}

		values = PreferencesValueUtil.getXMLSafeValues(values);

		Map<String, Preference> modifiedPreferences = getModifiedPreferences();

		Preference preference = modifiedPreferences.get(key);

		if ((preference != null) && preference.isReadOnly()) {
			throw new ReadOnlyException(key);
		}

		modifiedPreferences.put(key, new Preference(key, values));
	}

	public int size() {
		Map<String, Preference> preferences = getPreferences();

		return preferences.size();
	}

	@Override
	public void store() throws IOException, ValidatorException {
		if (_portletId == null) {
			throw new UnsupportedOperationException();
		}

		try {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				_companyId, _portletId);

			if (portlet != null) {
				PreferencesValidator preferencesValidator =
					PortalUtil.getPreferencesValidator(portlet);

				if (preferencesValidator != null) {
					preferencesValidator.validate(this);
				}
			}

			PortletPreferencesLocalServiceUtil.updatePreferences(
				getOwnerId(), getOwnerType(), _plid, _portletId, this);
		}
		catch (SystemException systemException) {
			throw new IOException(systemException);
		}
	}

	protected long getCompanyId() {
		return _companyId;
	}

	protected Map<String, Preference> getModifiedPreferences() {
		if (_modifiedPreferences == null) {
			_modifiedPreferences = new ConcurrentHashMap<>(
				_originalPreferences);
		}

		return _modifiedPreferences;
	}

	protected Map<String, Preference> getOriginalPreferences() {
		return _originalPreferences;
	}

	protected String getOriginalXML() {
		return _originalXML;
	}

	protected String getPortletId() {
		return _portletId;
	}

	protected String toXML() {
		if ((_modifiedPreferences == null) && (_originalXML != null)) {
			return _originalXML;
		}

		Map<String, Preference> preferences = getPreferences();

		if ((preferences == null) || preferences.isEmpty()) {
			return PortletConstants.DEFAULT_PREFERENCES;
		}

		Element portletPreferencesElement = new Element(
			"portlet-preferences", false);

		for (Map.Entry<String, Preference> entry : preferences.entrySet()) {
			Preference preference = entry.getValue();

			Element preferenceElement = portletPreferencesElement.addElement(
				"preference");

			preferenceElement.addElement("name", preference.getName());

			for (String value : preference.getValues()) {
				preferenceElement.addElement("value", value);
			}

			if (preference.isReadOnly()) {
				preferenceElement.addElement("read-only", Boolean.TRUE);
			}
		}

		return portletPreferencesElement.toXMLString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPreferencesImpl.class);

	private final long _companyId;
	private PortletPreferences _defaultPortletPreferences;
	private Map<String, Preference> _modifiedPreferences;
	private final Map<String, Preference> _originalPreferences;
	private final String _originalXML;
	private final long _ownerId;
	private final int _ownerType;
	private long _plid;
	private final String _portletId;

}