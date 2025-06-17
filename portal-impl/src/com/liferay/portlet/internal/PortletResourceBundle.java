/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.model.PortletInfo;
import com.liferay.portal.kernel.util.JavaConstants;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo Lundgren
 * @author Shuyang Zhou
 */
public class PortletResourceBundle extends ResourceBundle {

	public static Map<String, String> getPortletInfos(PortletInfo portletInfo) {
		if (portletInfo == null) {
			return Collections.emptyMap();
		}

		Map<String, String> portletInfos = new HashMap<>();

		String description = portletInfo.getDescription();

		if (description != null) {
			portletInfos.put(
				JavaConstants.JAKARTA_PORTLET_DESCRIPTION, description);
		}

		String keywords = portletInfo.getKeywords();

		if (keywords != null) {
			portletInfos.put(JavaConstants.JAKARTA_PORTLET_KEYWORDS, keywords);
		}

		String shortTitle = portletInfo.getShortTitle();

		if (shortTitle != null) {
			portletInfos.put(
				JavaConstants.JAKARTA_PORTLET_SHORT_TITLE, shortTitle);
		}

		String title = portletInfo.getTitle();

		if (title != null) {
			portletInfos.put(JavaConstants.JAKARTA_PORTLET_TITLE, title);
		}

		return portletInfos;
	}

	public PortletResourceBundle(
		ResourceBundle parentResourceBundle, Map<String, String> portletInfos) {

		parent = parentResourceBundle;
		_portletInfos = portletInfos;
	}

	@Override
	public boolean containsKey(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		if (_portletInfos.containsKey(key)) {
			return true;
		}

		if (parent != null) {
			return parent.containsKey(key);
		}

		return false;
	}

	@Override
	public Enumeration<String> getKeys() {
		if (parent == null) {
			return Collections.enumeration(_portletInfos.keySet());
		}

		Set<String> keys = new HashSet<>(parent.keySet());

		keys.addAll(_portletInfos.keySet());

		return Collections.enumeration(keys);
	}

	@Override
	public Locale getLocale() {
		if (parent == null) {
			return null;
		}

		return parent.getLocale();
	}

	@Override
	protected Object handleGetObject(String key) {
		if (key == null) {
			throw new NullPointerException();
		}

		if ((parent != null) && parent.containsKey(key)) {
			return parent.getString(key);
		}

		return _portletInfos.get(key);
	}

	@Override
	protected Set<String> handleKeySet() {
		return _portletInfos.keySet();
	}

	private final Map<String, String> _portletInfos;

}