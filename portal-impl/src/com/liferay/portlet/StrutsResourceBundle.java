/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.language.ResourceBundleEnumeration;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 */
public class StrutsResourceBundle extends ResourceBundle {

	public StrutsResourceBundle(String portletName, Locale locale) {
		_portletName = portletName;
		_locale = locale;

		setParent(LanguageResources.getResourceBundle(locale));
	}

	@Override
	public boolean containsKey(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		if (_keys.contains(key)) {
			key = _buildKey(key);
		}

		return parent.containsKey(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		Set<String> keys = new HashSet<>();

		for (String key : _keys) {
			if (parent.containsKey(_buildKey(key))) {
				keys.add(key);
			}
		}

		return new ResourceBundleEnumeration(keys, parent.getKeys());
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	protected Object handleGetObject(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		if (_keys.contains(key)) {
			key = _buildKey(key);
		}

		if (parent.containsKey(key)) {
			try {
				return parent.getObject(key);
			}
			catch (MissingResourceException missingResourceException) {
				if (_log.isDebugEnabled()) {
					_log.debug(missingResourceException);
				}

				return null;
			}
		}

		return null;
	}

	private String _buildKey(String key) {
		return StringBundler.concat(key, StringPool.PERIOD, _portletName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StrutsResourceBundle.class);

	private static final Set<String> _keys = SetUtil.fromArray(
		JavaConstants.JAKARTA_PORTLET_DESCRIPTION,
		JavaConstants.JAKARTA_PORTLET_KEYWORDS,
		JavaConstants.JAKARTA_PORTLET_LONG_TITLE,
		JavaConstants.JAKARTA_PORTLET_SHORT_TITLE,
		JavaConstants.JAKARTA_PORTLET_TITLE);

	private final Locale _locale;
	private final String _portletName;

}