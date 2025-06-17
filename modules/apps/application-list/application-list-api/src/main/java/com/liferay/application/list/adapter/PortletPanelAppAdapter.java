/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.adapter;

import com.liferay.application.list.BasePanelApp;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.util.JavaConstants;

import jakarta.portlet.PortletConfig;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * @author Adolfo Pérez
 */
public class PortletPanelAppAdapter extends BasePanelApp {

	public PortletPanelAppAdapter(
		String portletId, Supplier<Portlet> supplier) {

		_portletId = portletId;
		_supplier = supplier;
	}

	@Override
	public String getKey() {
		return "portlet-adapter-" + getPortletId();
	}

	@Override
	public String getLabel(Locale locale) {
		PortletConfig portletConfig = PortletConfigFactoryUtil.get(
			getPortletId());

		ResourceBundle resourceBundle = portletConfig.getResourceBundle(locale);

		Portlet portlet = getPortlet();

		String key =
			JavaConstants.JAKARTA_PORTLET_TITLE + StringPool.PERIOD +
				portlet.getPortletName();

		String value = LanguageUtil.get(resourceBundle, key);

		if (!key.equals(value)) {
			return value;
		}

		value = LanguageUtil.get(locale, key);

		if (!key.equals(value)) {
			return value;
		}

		String displayName = portlet.getDisplayName();

		if (!displayName.equals(portlet.getPortletName())) {
			return displayName;
		}

		return key;
	}

	@Override
	public Portlet getPortlet() {
		Portlet portlet = _portlet;

		if (portlet == null) {
			portlet = _supplier.get();

			_portlet = portlet;
		}

		return portlet;
	}

	@Override
	public String getPortletId() {
		return _portletId;
	}

	private volatile Portlet _portlet;
	private final String _portletId;
	private final Supplier<Portlet> _supplier;

}