/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.util;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import jakarta.portlet.PortletURL;

import java.text.Collator;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Preston Crary
 */
public class AssetPublisherAddItemHolder
	implements Comparable<AssetPublisherAddItemHolder> {

	public AssetPublisherAddItemHolder(
		String portletId, String name, ResourceBundle resourceBundle,
		Locale locale, PortletURL portletURL) {

		_portletId = portletId;
		_name = name;
		_locale = locale;
		_portletURL = portletURL;

		_collator = CollatorUtil.getInstance(locale);
		_modelResource = _getModelResource(resourceBundle);
	}

	@Override
	public int compareTo(
		AssetPublisherAddItemHolder assetPublisherAddItemHolder) {

		return _collator.compare(
			_modelResource, assetPublisherAddItemHolder._modelResource);
	}

	public String getModelResource() {
		return _modelResource;
	}

	public String getName() {
		return _name;
	}

	public String getPortletId() {
		return _portletId;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	/**
	 * @see AssetRendererFactory#getTypeName(Locale)
	 */
	private String _getModelResource(ResourceBundle resourceBundle) {
		String modelResourceNamePrefix =
			ResourceActionsUtil.getModelResourceNamePrefix();

		String key = modelResourceNamePrefix.concat(_name);

		String value = LanguageUtil.get(_locale, key, null);

		if ((value == null) && (resourceBundle != null)) {
			value = ResourceBundleUtil.getString(resourceBundle, key);
		}

		if (value == null) {
			value = _name;
		}

		return value;
	}

	private final Collator _collator;
	private final Locale _locale;
	private final String _modelResource;
	private final String _name;
	private final String _portletId;
	private final PortletURL _portletURL;

}