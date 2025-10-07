/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cookies;

import com.liferay.portal.kernel.settings.LocalizedValuesMap;

import java.util.Locale;

/**
 * @author Jürgen Kappler
 */
public class ConsentCookieType {

	public ConsentCookieType(
		LocalizedValuesMap descriptionMap, boolean hideFromEndUser, String name,
		boolean prechecked) {

		_descriptionMap = descriptionMap;
		_hideFromEndUser = hideFromEndUser;
		_name = name;
		_prechecked = prechecked;
	}

	public String getDescription(Locale locale) {
		return _descriptionMap.get(locale);
	}

	public LocalizedValuesMap getDescriptionMap() {
		return _descriptionMap;
	}

	public String getName() {
		return _name;
	}

	public boolean isHideFromEndUser() {
		return _hideFromEndUser;
	}

	public boolean isPrechecked() {
		return _prechecked;
	}

	private final LocalizedValuesMap _descriptionMap;
	private final boolean _hideFromEndUser;
	private final String _name;
	private final boolean _prechecked;

}